import java.io.*;
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;

/** This class provides methods to compute the group impact (# of shortest path covered by the group) over a directed graph.
 * Reference: Vatche Ishakian, D—ra Erdšs, Evimaria Terzi, and Azer Bestavros. "A Framework for the Evaluation and Management of Network Centrality." In SDM, pp. 427-438. 2012.
 * @author Feiyu Shi
 */
public class groupCentrality extends nodeCentrality{

	protected Set<String> groupSet = new HashSet<String>();
	
	/** Constructor with inputs of graph and addresses of files containing the source, destination and the group.
	 * 
	 * @param inputGraph 
	 * @param sourceAddr
	 * @param destinationAddr
	 * @param groupAddr
	 * @throws IOException
	 */
	public groupCentrality(Graph<String, DefaultEdge> inputGraph, String sourceAddr, String destinationAddr, String groupAddr) throws IOException {
		super(inputGraph, sourceAddr, destinationAddr);
		setParameters(groupAddr);
	}
	
	/** Constructor with inputs of graph and sets.
	 * 
	 * @param inputGraph
	 * @param srcSet
	 * @param dstnSet
	 * @param grpSet
	 */
	public groupCentrality(Graph<String, DefaultEdge> inputGraph, Set<String> srcSet, Set<String> dstnSet, Set<String> grpSet){
		super(inputGraph, srcSet, dstnSet);
		groupSet = grpSet;
	}
	
	/** Set group from file.
	 * 
	 * @param groupAddr The address of file that contains nodes of a group.
	 * @throws IOException
	 */
	private void setParameters (String groupAddr) throws IOException{

		// initiate sources and destinations

		File fin3 = new File(groupAddr);

		BufferedReader br3 = new BufferedReader(new FileReader(fin3));

		String line = null;
		
		while ((line = br3.readLine()) != null) {
			groupSet.add(line);
		}
					 
		br3.close();
	}
	
	/** The method that computes the group impact of the group from file.
	 * 
	 * @return the group impact.
	 */
	public long getGroupImpact() {
		Set<String> nodes = graph.vertexSet();
		if(!nodes.containsAll(groupSet)){
			throw new Error("The group nodes are not in this graph. Please check the group.");
		}else if(!nodes.containsAll(sourceSet)){
			throw new Error("The sources are not in this graph. Please check sources.");
		}else if(!nodes.containsAll(destinationSet)){
			throw new Error("The destinations are not in this graph. Please check destinations.");
		}else{
			Set<String> tempGroup = new HashSet<String>(groupSet.size());// empty set
			Iterator<String> groupItr = groupSet.iterator();// for all nodes in the group
			long sum = 0;
			while(groupItr.hasNext()){
				String currNode = groupItr.next();
				sum += getConditionalImpact(currNode, tempGroup);
				tempGroup.add(currNode);
			}
			return sum;
		}
		
	}
	
	/** The method that computes the group impact of the given group.
	 * 
	 * @param theGroup Given group.
	 * @return the group impact.
	 */
	public long getGroupImpact(Set<String> theGroup){
		Set<String> groupFromFile = groupSet;
		groupSet = theGroup;
		Set<String> nodes = graph.vertexSet();
		if(!nodes.containsAll(groupSet)){
			throw new Error("The group nodes are not in this graph. Please check the group.");
		}else if(!nodes.containsAll(sourceSet)){
			throw new Error("The sources are not in this graph. Please check sources.");
		}else if(!nodes.containsAll(destinationSet)){
			throw new Error("The destinations are not in this graph. Please check destinations.");
		}else{
			long theGroupImpact = getGroupImpact();
			groupSet = groupFromFile;
			return theGroupImpact;
		}
	}
	
	/** The methods that computes the conditional impact of the node with respect to the given group of node.
	 * 
	 * @param theNode node of interest.
	 * @param theGroup usually the partial group of given group.
	 * @return the conditional impact.
	 */
	protected long getConditionalImpact(String theNode, Set<String> theGroup){
		if(theGroup.isEmpty()){
			return getNodeImpact(theNode);
		}else{
			HashMap<String, Long> impact = getAllConditionalNodeImpact(theGroup);
			return impact.get(theNode);
		}
	}
	
	/** The method that is used to compute the conditional impact.
	 * 
	 * @param currGroup the group of interest
	 * @return the hash map of conditional impact of all nodes
	 */
	private HashMap<String, Long> getAllConditionalNodeImpact(Set<String> currGroup){
		// definition: prefix, suffix, PLIST
		HashMap<String, Long> prefix = new HashMap<String, Long>();
		HashMap<String, Long> suffix = new HashMap<String, Long>();
		HashMap<String, HashMap<String, Long>> PLIST = new HashMap<String, HashMap<String, Long>>();
		HashMap<String, Long> impact = new HashMap<String, Long>();
		Set<String> nodes = graph.vertexSet();
				
		// initiate PLIST, PLIST(v,v) = 1, PLIST(u,v) = 0
		Iterator<String> itr2 = nodes.iterator();
		while(itr2.hasNext()){
			String currNode = itr2.next();
			HashMap<String, Long> temp = new HashMap<String, Long>();
			Iterator<String> itr3 = nodes.iterator();
			while(itr3.hasNext()){
			String tempNode = itr3.next();
				if (tempNode == currNode){
					temp.put(currNode, (long) 1);
				}else{
					temp.put(tempNode, (long) 0);
				}
			}
					
			PLIST.put(currNode, temp);
		}
						
		// temporary for DAG
		DirectedGraph<String,DefaultEdge> dag = (DirectedGraph<String,DefaultEdge>) graph;
		ArrayList<String> topoList = new ArrayList<String>(nodes.size());
				
		Iterator<String> sourceOrder = sourceSet.iterator();
				
		while(sourceOrder.hasNext()){
			Iterator<String> traverseOrder = nodes.iterator();// initialized to random order
			if(graph instanceof DirectedGraph<?, ?>){
				traverseOrder = new TopologicalOrderIterator<String, DefaultEdge>((DirectedGraph<String, DefaultEdge>) graph);// need to be fixed
			}else{
				traverseOrder = new BreadthFirstIterator<String, DefaultEdge>(graph);// need to choose start vertex
			}
					
			String currSource = sourceOrder.next();
			HashMap<String, Long> tempPathLength = new HashMap<String, Long>();
			tempPathLength.put(currSource, (long) 0);
					
			int topoIndex = 0;
			while(traverseOrder.hasNext()){
						
				String currNode = traverseOrder.next();
				topoList.add(topoIndex, currNode);
				// prefix & PLIST
						
				// path length
				DijkstraShortestPath<String,DefaultEdge> sp = new DijkstraShortestPath<String,DefaultEdge>(dag, currSource, currNode);
				double pathLength= sp.getPathLength();
				if (pathLength == Double.POSITIVE_INFINITY){pathLength = 0;}
				tempPathLength.put(currNode, (long)pathLength);
				// get parents of current node
				Set<DefaultEdge> inCommingEdges = dag.incomingEdgesOf(currNode);
				Iterator<DefaultEdge> inCommingEdgesOrder = inCommingEdges.iterator();
				Set<String> parents = new HashSet<String>(); 
				while(inCommingEdgesOrder.hasNext()){
					parents.add(dag.getEdgeSource(inCommingEdgesOrder.next()));
				}
				// prefix recurrence relation:
				Iterator<String> parentOrder = parents.iterator();
				long sum = 0;
				while(parentOrder.hasNext()){
					// check if parent is on a shortest path
					String tempParent = parentOrder.next();
					if (tempPathLength.containsKey(tempParent)){
						if(tempPathLength.get(currNode) - tempPathLength.get(tempParent) == 1){
							// conditional prefix
							if (!currGroup.contains(tempParent)){
								sum += prefix.get(tempParent);
							}
						}
					}
				}

				prefix.put(currNode, sum);
						
				// initiate prefix, prefix(source) = 1
				if (currNode.equals(currSource)){
					prefix.put(currNode, (long) 1);
				}

				// PLIST
				HashMap<String, Long> currHashMap = PLIST.get(currNode);
				for(int i = 0; i < topoIndex; i++){

					String currAncestor = topoList.get(i);
					// PLIST recurrent relation:
					long sum2 = 0;
					Iterator<String> parentOrder2 = parents.iterator();
					while(parentOrder2.hasNext()){
						// check if parent is on a shortest path
						String currParent = parentOrder2.next();
						if (tempPathLength.containsKey(currParent)){
							if(tempPathLength.get(currNode) - tempPathLength.get(currParent) == 1){
								//conditional PLIST
								if(!currGroup.contains(currParent)){
									HashMap<String, Long> tempHashMap = PLIST.get(currParent);
									sum2 += tempHashMap.get(currAncestor);
								}
							}
						}
					}
					currHashMap.put(currAncestor, sum2);
				}
				PLIST.put(currNode, currHashMap);
				topoIndex ++;
			}
					
			// suffix 
			Iterator<String> itr = nodes.iterator();
			while(itr.hasNext()){
				String currNode = itr.next();
				Iterator<String> itr0 = destinationSet.iterator();
				long sum = 0;
				while(itr0.hasNext()){
					sum += PLIST.get(itr0.next()).get(currNode);
				}
				suffix.put(currNode, sum);
				// aggregate impacts of different sources
				if (!impact.containsKey(currNode)){
					impact.put(currNode, prefix.get(currNode)*suffix.get(currNode));
				}else{
					long temp = impact.get(currNode);
					impact.put(currNode, temp + prefix.get(currNode)*suffix.get(currNode));
				}
			}
		}
		return impact;
	}
	
}
