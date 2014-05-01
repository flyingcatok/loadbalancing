import java.io.*;
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;

/** This class provides methods that can divide a group of nodes into two, whose group impact difference is minimized.
 * @author Feiyu Shi
 */
public class loadBalancing extends groupCentrality {

	/** Constructor with address of files containing source, destination and group.
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
	
	/** Constructor with sets of sources, destinations and the group.
	 * 
	 * @param inputGraph
	 * @param srcSet
	 * @param dstnSet
	 * @param grpSet
	 */
	public loadBalancing(Graph<String, DefaultEdge> inputGraph, Set<String> srcSet, Set<String> dstnSet, Set<String> grpSet){
		super(inputGraph, srcSet, dstnSet, grpSet);
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
	
	/** This method gives all possible assignments of the load balancing problem, but it takes the longest of time.
	 * @return A list of sets, together with its complement set (not in the list) forms the answer.
	 */
	public List<Set<String>> getGroupAssignment_bruteForce (){// may have more efficient implementation
		Set<String> nodes = graph.vertexSet();
		if(!nodes.containsAll(groupSet)){
			throw new Error("The group nodes are not in this graph. Please check the group.");
		}else if(!nodes.containsAll(sourceSet)){
			throw new Error("The sources are not in this graph. Please check sources.");
		}else if(!nodes.containsAll(destinationSet)){
			throw new Error("The destinations are not in this graph. Please check destinations.");
		}else{
			List<Set<String>> ps = powerSet(groupSet);
			List<Long> gc = new ArrayList<Long>(ps.size());
			List<Set<String>> res = new ArrayList<Set<String>>();
			
			for(int i = 0; i< ps.size(); i++){
				gc.add(i, getGroupImpact(ps.get(i)));
			}
			
			long min = Long.MAX_VALUE;
			int sz = gc.size() / 2;
			for(int i = 0; i< sz; i++){
				long diff = Math.abs(gc.get(i) - gc.get(gc.size()-i-1));
				if (diff <= min){
					min = diff;
				}
			}
			
			for(int i = 0; i< sz; i++){
				long diff = Math.abs(gc.get(i) - gc.get(gc.size()-i-1));
				if (diff == min){
					res.add(ps.get(i));
				}
			}
			return res;
		}
		
	}
	
	/** Helper function for greedy algorithm. for recursive use.
	 * 
	 * @param g1 the first group of nodes. Initially is the whole group
	 * @param g2 the second group of nodes. Initially is the empty group
	 * @param impactDifference the difference of impacts of g1 and g2
	 * @return a possible assignment of load balancing
	 */
	private Set<String> helper_greedy(Set<String> g1, Set<String> g2, long impactDifference){
//		List<Set<String>> res = new ArrayList<Set<String>>();
		Set<String> res = new HashSet<String>();
		
		Iterator<String> itr = g1.iterator();
		HashMap<String, Long> newDifferenceAbs = new HashMap<String, Long>(g1.size());
		HashMap<String, Long> newDifference = new HashMap<String, Long>(g1.size());
		
		while(itr.hasNext()){
			String currNode = itr.next();
			Set<String> tempG1 = new HashSet<String>(g1);
			tempG1.remove(currNode);
			// heuristic: sumOfImpact
			long temp = getSumOfConditionalImpactOfG1G2(currNode, tempG1, g2);
			newDifference.put(currNode,impactDifference-temp);
			newDifferenceAbs.put(currNode, Math.abs(impactDifference-temp));	
		}

		List<String> nodesWithMaxOfSumOfImpact = getKeysWithMinValuesFromMap1(newDifferenceAbs);
		// add some randomness
		Collections.shuffle(nodesWithMaxOfSumOfImpact);

		String theNode = nodesWithMaxOfSumOfImpact.get(0);
		// remove the node from g1 and add it to g2
			
		g1.remove(theNode);
		g2.add(theNode);
//		long difference1 = Math.abs(getGroupImpact(g1) - getGroupImpact(g2));
		long difference = newDifference.get(theNode);
		if (Math.abs(difference) <= Math.abs(impactDifference) && !g1.isEmpty()){
			return helper_greedy(g1, g2, difference);
		}else{
			g1.add(theNode);
			res = g1;
			return res;
		}	
		
	}
	
	/** This method gives one possible assignment of the load balancing problem.
	 * 
	 * @return one assignment of sets.
	 */
	public Set<String> getGroupAssignment_greedy(){
		Set<String> nodes = graph.vertexSet();
		if(!nodes.containsAll(groupSet)){
			throw new Error("The group nodes are not in this graph. Please check the group.");
		}else if(!nodes.containsAll(sourceSet)){
			throw new Error("The sources are not in this graph. Please check sources.");
		}else if(!nodes.containsAll(destinationSet)){
			throw new Error("The destinations are not in this graph. Please check destinations.");
		}else{
			Set<String> g1 = new HashSet<String>(groupSet);// full set
			Set<String> g2 = new HashSet<String>(groupSet.size());// empty set
			long  impactDifference = getGroupImpact(g1);
			return helper_greedy(g1, g2, impactDifference);
		}
		
	}
	
//	/** This methods returns a list of keys with max values in the map.
//	 * 
//	 * @param map Given map.
//	 * @return Keys with largest integer value.
//	 */
//	private static List<String> getKeysWithMaxValuesFromMap(Map<String, Long> map){
//		List<String> res = new ArrayList<String>();
//		Collection<Long> valueSet = map.values();
//		long minValue = Collections.max(valueSet);
//		for(String entry : map.keySet()){
//			if(map.get(entry).equals(minValue)) res.add(entry);
//		}
//		return res;
//	}
	
	/** This methods returns a list of keys with min values in the map. The key type is String.
	 * 
	 * @param map
	 * @return
	 */
	private static List<String> getKeysWithMinValuesFromMap1(Map<String, Long> map){
		List<String> res = new ArrayList<String>();
		Collection<Long> valueSet = map.values();
		long minValue = Collections.min(valueSet);
//		if(minValue < 0){
//			valueSet.remove(minValue);
//		}
//		while(minValue < 0 && !valueSet.isEmpty()){
//			minValue = Collections.min(valueSet);
//		}
		for(String entry : map.keySet()){
			if(map.get(entry).equals(minValue)) res.add(entry);
		}
		return res;
	}
	
	/** This methods returns a list of keys with min values in the map. The key type is Set<String>.
	 * 
	 * @param map
	 * @return
	 */
	private static List<Set<String>> getKeysWithMinValuesFromMap(Map<Set<String>, Long> map){
		List<Set<String>> res = new ArrayList<Set<String>>();
		Collection<Long> valueSet = map.values();
		long minValue = Collections.min(valueSet);
		for(Set<String> entry : map.keySet()){
			if(map.get(entry).equals(minValue)) res.add(entry);
		}
		return res;
	}
	
	/** This method returns the sum of conditional impacts of the node w.r.t g1 and g2
	 * 
	 * @param theNode the given node
	 * @param G1 group 1
	 * @param G2 group 2
	 * @return the sum of conditional impacts of the node w.r.t g1 and g2
	 */
	private Long getSumOfConditionalImpactOfG1G2(String theNode, Set<String> G1, Set<String> G2){
		return getConditionalImpact(theNode, G1) + getConditionalImpact(theNode, G2);
	}

	/** Helper function for greedy search algorithm. for recursive use.
	 * 
	 * @param theNode The picked node which is moved from group 1 to group 2
	 * @param g1 group 1 not containing theNode
	 * @param g2 group 2 containing theNode
	 * @param impactDifference the difference of impacts of two groups after the change of theNode
	 * @param assgns possible assignments with its difference of impacts. but not every one is the best.
	 * @param visited already evaluated sets of nodes.
	 */
	private void helper_greedySearch (String theNode, Set<String> g1, Set<String> g2, long impactDifference, HashMap<Set<String>, Long> assgns, Set<Set<String>> visited){

		Iterator<String> itr = g1.iterator();
		HashMap<String, Long> newDifference = new HashMap<String, Long>(g1.size());
		HashMap<String, Long> newDifferenceAbs = new HashMap<String, Long>(g1.size());
		while(itr.hasNext()){
			String currNode = itr.next();
			Set<String> tempG1 = new HashSet<String>(g1);
			tempG1.remove(currNode);
			// heuristic: sumOfImpact
			long temp = getSumOfConditionalImpactOfG1G2(currNode, tempG1, g2);
			newDifference.put(currNode, impactDifference - temp);
			newDifferenceAbs.put(currNode, Math.abs(temp - impactDifference));// find the sumOfImpact which is closed to impactDifference
		}
		List<String> nodesWithMaxOfSumOfImpact = getKeysWithMinValuesFromMap1(newDifferenceAbs); // actually the min of difference
		Iterator<String> itr2 = nodesWithMaxOfSumOfImpact.iterator();
		while(itr2.hasNext()){
			String chosenNode = itr2.next();
			Set<String> G1 = new HashSet<String>(g1);// full set copy
			Set<String> G2 = new HashSet<String>(g2);// empty set copy
			// remove the node from g1 and add it to g2
			G1.remove(chosenNode);
			G2.add(chosenNode);

			if(!visited.contains(G1) && !visited.contains(G2)){ // if the new G1 & G2 are not evaluated, proceed.

//				long difference = Math.abs(getGroupImpact(G1) - getGroupImpact(G2));
				long difference = newDifference.get(chosenNode);
				if(Math.abs(difference) <= Math.abs(impactDifference) && !G1.isEmpty()){
					if(difference == 0 || Math.abs(difference) == Math.abs(impactDifference)){
						assgns.put(G2, Math.abs(difference));
						visited.add(G2);
					}
					helper_greedySearch(chosenNode, G1, G2, difference, assgns, visited);
				}
				else{
					G1.add(chosenNode);
					G2.remove(chosenNode);
					visited.add(G2);
					assgns.put(G2, Math.abs(impactDifference));
				}
			}
		}
	} 
	
	/** This method upgrades the greedy algorithm. search with pruning.
	 * It could return more possible assignments if there are more than one correct assignments.
	 * 
	 * @return a list of possible assignments. May not get the correct assignment.
	 */
	public List<Set<String>> getGroupAssignment_greedySearch (){
		Set<String> nodes = graph.vertexSet();
		if(!nodes.containsAll(groupSet)){
			throw new Error("The group nodes are not in this graph. Please check the group.");
		}else if(!nodes.containsAll(sourceSet)){
			throw new Error("The sources are not in this graph. Please check sources.");
		}else if(!nodes.containsAll(destinationSet)){
			throw new Error("The destinations are not in this graph. Please check destinations.");
		}else{
			HashMap<Set<String>, Long> assgns = new HashMap<Set<String>, Long>();
			Set<Set<String>> visited = new HashSet<Set<String>>();
			
			Set<String> g1 = new HashSet<String>(groupSet);// full set
			Set<String> g2 = new HashSet<String>(groupSet.size());// empty set
			
			// special case: difference = 0 all the time
			long differenceTop = getGroupImpact(g1);
			if(differenceTop == 0){
				assgns.put(g2, differenceTop);
				visited.add(g2);
			}
			
			Iterator<String> itr = g1.iterator();
			HashMap<String, Long> newDifference = new HashMap<String, Long>(g1.size());
			HashMap<String, Long> newDifferenceAbs = new HashMap<String, Long>(g1.size());
			while(itr.hasNext()){
				String currNode = itr.next();
				Set<String> tempG1 = new HashSet<String>(g1);
				tempG1.remove(currNode);
				// heuristic: sumOfImpact
				long temp = getSumOfConditionalImpactOfG1G2(currNode, tempG1, g2);
				newDifference.put(currNode, differenceTop-temp);
				newDifferenceAbs.put(currNode, Math.abs(differenceTop-temp));
			}
			
			List<String> nodesWithMaxOfSumOfImpact = getKeysWithMinValuesFromMap1(newDifferenceAbs);
			Iterator<String> itr2 = nodesWithMaxOfSumOfImpact.iterator();
			
			while(itr2.hasNext()){
				String theNode = itr2.next();
				Set<String> G1 = new HashSet<String>(g1);// full set copy
				Set<String> G2 = new HashSet<String>(g2);// empty set copy
				G1.remove(theNode);
				G2.add(theNode);

				if(!visited.contains(G1) && !visited.contains(G2)){

//					long difference = Math.abs(getGroupImpact(G1) - getGroupImpact(G2));
					long difference = newDifference.get(theNode);
					if(difference == 0 || Math.abs(difference) == differenceTop){
						assgns.put(G2, Math.abs(difference));
						visited.add(G2);
						helper_greedySearch(theNode, G1, G2, difference, assgns, visited);
					}else{
						helper_greedySearch(theNode, G1, G2, difference, assgns, visited);
					}
				}
			}
			List<Set<String>> res = getKeysWithMinValuesFromMap(assgns);
			// special check for the top case
			boolean flag = true;
			Collection<Long> v = assgns.values();
			Iterator<Long> itrv = v.iterator();
			while(itrv.hasNext()){
				long temp = itrv.next();
				if (temp != differenceTop){
					flag = false;
				}
			}
			if(flag && !res.contains(g2)){
				res.add(g2);
			}
			return res;
		}

	}
	
	private void helper_fullSearch (String theNode, Set<String> g1, Set<String> g2, long impactDifference, HashMap<Set<String>, Long> assgns, Set<Set<String>> visited){
		
		Iterator<String> itr = g1.iterator();
		while(itr.hasNext()){
			String chosenNode = itr.next();
			Set<String> G1 = new HashSet<String>(g1);// full set copy
			Set<String> G2 = new HashSet<String>(g2);// empty set copy
			// remove the node from g1 and add it to g2
			G1.remove(chosenNode);
			G2.add(chosenNode);

			if(!visited.contains(G1) && !visited.contains(G2)){ // if the new G1 & G2 are not evaluated, proceed.

				long difference = getGroupImpact(G1) - getGroupImpact(G2);
				
				if(Math.abs(difference) <= Math.abs(impactDifference) && !G1.isEmpty()){
					if(difference == 0 || Math.abs(difference) == Math.abs(impactDifference)){
						assgns.put(G2, Math.abs(difference));
						visited.add(G2);
					}
					helper_fullSearch(chosenNode, G1, G2, difference, assgns, visited);
				}
				else{
					G1.add(chosenNode);
					G2.remove(chosenNode);
					visited.add(G2);
					assgns.put(G2, Math.abs(impactDifference));
				}
			}
		}
	} 
	
	/** This method relax the constraint that only pick the node whose sum of conditional impacts which is closest to the impact difference.
	 *  search with pruning. can find all the solutions
	 * @return a list of all possible assignments. 
	 */
	public List<Set<String>> getGroupAssignment_fullSearch (){
		Set<String> nodes = graph.vertexSet();
		if(!nodes.containsAll(groupSet)){
			throw new Error("The group nodes are not in this graph. Please check the group.");
		}else if(!nodes.containsAll(sourceSet)){
			throw new Error("The sources are not in this graph. Please check sources.");
		}else if(!nodes.containsAll(destinationSet)){
			throw new Error("The destinations are not in this graph. Please check destinations.");
		}else{
			HashMap<Set<String>, Long> assgns = new HashMap<Set<String>, Long>();
			Set<Set<String>> visited = new HashSet<Set<String>>();
			
			Set<String> g1 = new HashSet<String>(groupSet);// full set
			Set<String> g2 = new HashSet<String>(groupSet.size());// empty set
			
			// special case
			long differenceTop = getGroupImpact(g1) - getGroupImpact(g2);
			if(differenceTop == 0){
				assgns.put(g2, differenceTop);
				visited.add(g2);
			}
			
			Iterator<String> itr = g1.iterator();
			while(itr.hasNext()){
				String theNode = itr.next();
				Set<String> G1 = new HashSet<String>(g1);// full set copy
				Set<String> G2 = new HashSet<String>(g2);// empty set copy
				G1.remove(theNode);
				G2.add(theNode);

				if(!visited.contains(G1) && !visited.contains(G2)){

					long difference = getGroupImpact(G1) - getGroupImpact(G2);
					if(difference == 0 || Math.abs(difference) == differenceTop){
						assgns.put(G2, Math.abs(difference));
						visited.add(G2);
						helper_fullSearch(theNode, G1, G2, difference, assgns, visited);
					}else{
						helper_fullSearch(theNode, G1, G2, difference, assgns, visited);
					}
				}
			}

			List<Set<String>> res = getKeysWithMinValuesFromMap(assgns);
			// special check for top case
			boolean flag = true;
			Collection<Long> v = assgns.values();
			Iterator<Long> itrv = v.iterator();
			while(itrv.hasNext()){
				long temp = itrv.next();
				if (temp != differenceTop){
					flag = false;
				}
			}
			if(flag && !res.contains(g2)){
				res.add(g2);
			}
			return res;
		}
	}
}
