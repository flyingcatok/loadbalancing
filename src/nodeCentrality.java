
import java.util.*;
import java.io.*;

import org.jgrapht.*;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;

/** This class provided methods to compute the impact of certain node (# of shortest path covered by the node) over a directed graph.
 * @author Feiyu Shi
 */
public class nodeCentrality {
	protected Graph<String, DefaultEdge> graph;
//	private Iterator<String> traverseOrder;// may be deleted later
	protected Set<String> sourceSet = new HashSet<String>();
	protected Set<String> destinationSet = new HashSet<String>();
	private HashMap<String, Integer> impact = new HashMap<String, Integer>();
	
	/** Constructor.
	 * 
	 * @param inputGraph Given graph, now only accepts DAG.
	 * @param sourceAddr The address of file that contains the source nodes.
	 * @param destinationAddr The address of file that contains the destination nodes.
	 * @throws IOException File not found.
	 *
	 */
	public nodeCentrality(Graph<String, DefaultEdge> inputGraph, String sourceAddr, String destinationAddr) throws IOException {
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
	public nodeCentrality(Graph<String, DefaultEdge> inputGraph, Set<String> srcSet, Set<String> dstnSet){
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
	private HashMap<String, Integer> getAllNodeImpact(){
		// definition: prefix, suffix, PLIST
		HashMap<String, Integer> prefix = new HashMap<String, Integer>();
		HashMap<String, Integer> suffix = new HashMap<String, Integer>();
		HashMap<String, HashMap<String, Integer>> PLIST = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, Integer> impact = new HashMap<String, Integer>();
		Set<String> nodes = graph.vertexSet();
		
		// initiate PLIST, PLIST(v,v) = 1, PLIST(u,v) = 0
		Iterator<String> itr2 = nodes.iterator();
		while(itr2.hasNext()){
			String currNode = itr2.next();
			HashMap<String, Integer> temp = new HashMap<String, Integer>();
			Iterator<String> itr3 = nodes.iterator();
			while(itr3.hasNext()){
				String tempNode = itr3.next();
				if (tempNode == currNode){
					temp.put(currNode, 1);
				}else{
					temp.put(tempNode, 0);
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
			HashMap<String, Integer> tempPathLength = new HashMap<String, Integer>();
			tempPathLength.put(currSource, 0);
			
			int topoIndex = 0;
			while(traverseOrder.hasNext()){
				
				String currNode = traverseOrder.next();
				topoList.add(topoIndex, currNode);
				// prefix & PLIST
				
				// path length
				DijkstraShortestPath<String,DefaultEdge> sp = new DijkstraShortestPath<String,DefaultEdge>(dag, currSource, currNode);
				double pathLength= sp.getPathLength();
				if (pathLength == Double.POSITIVE_INFINITY){pathLength = 0;}
				tempPathLength.put(currNode, (int)pathLength);
				// get parents of current node
				Set<DefaultEdge> inCommingEdges = dag.incomingEdgesOf(currNode);
				Iterator<DefaultEdge> inCommingEdgesOrder = inCommingEdges.iterator();
				Set<String> parents = new HashSet<String>(); 
				while(inCommingEdgesOrder.hasNext()){
					parents.add(dag.getEdgeSource(inCommingEdgesOrder.next()));
				}
				// prefix recurrence relation:
				Iterator<String> parentOrder = parents.iterator();
				int sum = 0;
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
					prefix.put(currNode, 1);
				}
					
				// PLIST
				HashMap<String, Integer> currHashMap = PLIST.get(currNode);
				for(int i = 0; i < topoIndex; i++){

					String currAncestor = topoList.get(i);
					// PLIST recurrent relation:
					int sum2 = 0;
					Iterator<String> parentOrder2 = parents.iterator();
					while(parentOrder2.hasNext()){
						// check if parent is on a shortest path
						String currParent = parentOrder2.next();
						if (tempPathLength.containsKey(currParent)){
							if(tempPathLength.get(currNode) - tempPathLength.get(currParent) == 1){
								HashMap<String, Integer> tempHashMap = PLIST.get(currParent);
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
				int sum = 0;
				while(itr0.hasNext()){
					sum += PLIST.get(itr0.next()).get(currNode);
				}
				suffix.put(currNode, sum);
				// aggregate impacts of different sources
				if (!impact.containsKey(currNode)){
					impact.put(currNode, prefix.get(currNode)*sum);
				}else{
					int temp = impact.get(currNode);
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
	public int getNodeImpact(String theNode){
		Set<String> nodes = graph.vertexSet();
		if(!nodes.contains(theNode)){
			throw new Error("The node is not in this graph. Please try again.");
		}else{
			if (impact.isEmpty()){
				impact = getAllNodeImpact();// execute only once
			}
				return impact.get(theNode);
		}
	}

}
