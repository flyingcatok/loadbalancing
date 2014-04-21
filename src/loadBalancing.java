import java.io.*;
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;

/** This class provides methods that can divide a group of nodes into two, whose group impact difference is minimum.
 * @author Feiyu Shi
 */
public class loadBalancing extends groupCentrality {
	
	/** Constructor.
	 * 
	 * @param inputGraph Input directed graph.
	 * @param sourceAddr 
	 * @param destinationAddr
	 * @param groupAddr
	 * @throws IOException
	 */
	public loadBalancing(Graph<String, DefaultEdge> inputGraph,
			String sourceAddr, String destinationAddr, String groupAddr)
			throws IOException {
		super(inputGraph, sourceAddr, destinationAddr, groupAddr);
	}
	
	/** This method gives a power set of given set. 
	 * Reference: http://rosettacode.org/wiki/Power_set#Java
	 * @param list The given set.
	 * @return A list of all subsets.
	 */
	private static List<Set<String>> powerSet(Collection<String> list) {
		  List<Set<String>> ps = new ArrayList<Set<String>>();
		  ps.add(new HashSet<String>());   // add the empty set
		 
		  // for every item in the original list
		  for (String item : list) {
		    List<Set<String>> newPs = new ArrayList<Set<String>>();
		 
		    for (Set<String> subset : ps) {
		      // copy all of the current powerset's subsets
		      newPs.add(subset);
		 
		      // plus the subsets appended with the current item
		      Set<String> newSubset = new HashSet<String>(subset);
		      newSubset.add(item);
		      newPs.add(newSubset);
		    }
		 
		    // power set is now power set of list.subList(0, list.indexOf(item)+1)
		    ps = newPs;
		  }
		  return ps;
		}
	
	/** This method gives all possible assignments of the load balancing problem.
	 * @return A list of sets, together with its complement set (not in the list) forms the answer.
	 */
	public List<Set<String>> getGroupAssignment_baseLine (){// may have more efficient implementation
		List<Set<String>> ps = powerSet(groupSet);
		List<Integer> gc = new ArrayList<Integer>(ps.size());
		List<Set<String>> res = new ArrayList<Set<String>>();
		
		for(int i = 0; i< ps.size(); i++){
			gc.add(i, getGroupImpact(ps.get(i)));
		}
		
		int min = Integer.MAX_VALUE;
		int sz = gc.size() / 2;
		for(int i = 0; i< sz; i++){
			int diff = Math.abs(gc.get(i) - gc.get(gc.size()-i-1));
			if (diff <= min){
				min = diff;
			}
		}
		
		for(int i = 0; i< sz; i++){
			int diff = Math.abs(gc.get(i) - gc.get(gc.size()-i-1));
			if (diff == min){
				res.add(ps.get(i));
			}
		}
		return res;
	}
	
	public static void main(String[] args) throws IOException {
		graphGenerator generator = new graphGenerator();
		loadBalancing c = new loadBalancing(generator.graph, "./data/source_default.txt", "./data/destination_default.txt", "./data/group_default.txt");
		
		// baseline method
		long beginTime = System.currentTimeMillis();
		List<Set<String>> assgn = c.getGroupAssignment_baseLine();
		long totalTime = System.currentTimeMillis() - beginTime;
		System.out.println("Total used time for baseline method: " + totalTime + "ms");
		// for display
		System.out.println("Number of possible Assignment: " + assgn.size());
		for (int i = 0; i < assgn.size(); i++) {
			Set<String> groupNodes = new HashSet<String>(c.groupSet);
			System.out.println("Assignment " + (i+1) + ": ");
			System.out.println("Set " + assgn.get(i).toString() + " Impact: " + c.getGroupImpact(assgn.get(i)) + " ");
			groupNodes.removeAll(assgn.get(i));
			System.out.println("The other set: " + groupNodes + " Impact: " + c.getGroupImpact(groupNodes));
			System.out.println();
		}

	}

}
