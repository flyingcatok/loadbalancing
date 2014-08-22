package centrality;

import java.util.*;
import java.io.*;

import org.jgrapht.*;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;

/** This class provided methods to compute the impact of certain node (# of shortest path covered by the node) over a directed graph.
 * Reference: Vatche Ishakian, D—ra Erdšs, Evimaria Terzi, and Azer Bestavros. "A Framework for the Evaluation and Management of Network Centrality." In SDM, pp. 427-438. 2012.
 * @author Feiyu Shi
 */
public class NodeCentrality {
	
	private Graph<String, DefaultEdge> graph;
	private Set<String> sourceSet = new HashSet<String>();
	private Set<String> destinationSet = new HashSet<String>();
	private HashMap<String, Long> impact = new HashMap<String, Long>();
	
	public Graph<String, DefaultEdge> getGraph(){
		return graph;
	}
	
	public Set<String> getSourceSet(){
		return sourceSet;
	}
	
	public Set<String> getDestinationSet(){
		return destinationSet;
	}
	
	/** Constructor.
	 * 
	 * @param inputGraph Given graph, now only accepts DAG.
	 * @param sourceAddr The address of file that contains the source nodes.
	 * @param destinationAddr The address of file that contains the destination nodes.
	 * @throws IOException File not found.
	 *
	 */
	public NodeCentrality(Graph<String, DefaultEdge> inputGraph, String sourceAddr, String destinationAddr) throws IOException {
		// initiate graph
		graph = (DirectedGraph<String,DefaultEdge>) inputGraph;
		setParameters(sourceAddr, destinationAddr);
	}
	
	/** Constructor of variance.
	 * 
	 * @param inputGraph Given graph.
	 * @param srcSet Set of source nodes.
	 * @param dstnSet Set of destination nodes.
	 */
	public NodeCentrality(Graph<String, DefaultEdge> inputGraph, Set<String> srcSet, Set<String> dstnSet){
		graph = (DirectedGraph<String,DefaultEdge>) inputGraph;
		sourceSet = srcSet;
		destinationSet = dstnSet;
	}
	
	/** The method that set the values of source set and destination set.
	 * 
	 * @param sourceAddr The address of file that contains the source nodes.
	 * @param destinationAddr The address of file that contains the destination nodes.
	 * @throws IOException File not found.
	 */
	private void setParameters (String sourceAddr, String destinationAddr) throws IOException{
		// initiate sources and destinations
		File fin1 = new File(sourceAddr);
		File fin2 = new File(destinationAddr);

		BufferedReader br1 = new BufferedReader(new FileReader(fin1));
		BufferedReader br2 = new BufferedReader(new FileReader(fin2));

		String line = null;
				
		while ((line = br1.readLine()) != null) {
			sourceSet.add(line);
		}
			 
		br1.close();
				
		while ((line = br2.readLine()) != null) {
			destinationSet.add(line);
		}
			 
		br2.close();
	}
	
	/**
	 * This method computes the impact of every node in the graph.
	 */
	private HashMap<String, Long> getAllNodeImpact(){
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
							sum += prefix.get(tempParent);
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
								HashMap<String, Long> tempHashMap = PLIST.get(currParent);
								sum2 += tempHashMap.get(currAncestor);
							}
						}
					}
					currHashMap.put(currAncestor, sum2);
				}
				PLIST.put(currNode, currHashMap);
				topoIndex ++;
			}
			
			// suffix & impact
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
					impact.put(currNode, prefix.get(currNode)*sum);
				}else{
					long temp = impact.get(currNode);
					impact.put(currNode, temp + prefix.get(currNode)*sum);
				}
			}
		}
		return impact;
	}
	
	/** This method computes the impact of given node.
	 * 
	 * @param theNode Node of interest.
	 * @return the impact.
	 */
	public long getNodeImpact(String theNode){
		Set<String> nodes = graph.vertexSet();
		if(!nodes.contains(theNode)){
			throw new Error("The requested node is not in this graph. Please try again.");
		}else if(!nodes.containsAll(sourceSet)){
			throw new Error("The sources are not in this graph. Please check sources.");
		}else if(!nodes.containsAll(destinationSet)){
			throw new Error("The destinations are not in this graph. Please check destinations.");
		}else{
			if (impact.isEmpty()){
				impact = getAllNodeImpact();// execute only once
			}
				return impact.get(theNode);
		}
	}

}
