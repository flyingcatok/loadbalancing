import java.io.*;
import java.util.*;

/** This class contains the test on three algorithms for DAG load balancing problem.
 * 
 * @author Feiyu Shi
 *
 */
public class test {
	
	static List<List<String>> res_bruteForce = new ArrayList<List<String>>();
	static List<List<String>> res_greedy = new ArrayList<List<String>>();
	static List<List<String>> res_greedySearch = new ArrayList<List<String>>();
	static List<List<String>> res_fullSearch = new ArrayList<List<String>>();
	
	/** This method computes the given set's complement set.
	 * 
	 * @param whole the whole set
	 * @param set the given set
	 * @return the complement set
	 */
	private static Set<String> getComplementSet(Set<String> whole, Set<String> set){
		Set<String> w = new HashSet<String>(whole);
		Set<String> s = new HashSet<String>(set);
		w.removeAll(s);
		return w;
	}
	
	/** This method tests bruteForce, greedy, greedy search and full search algorithms on DAG load balancing problem instances. 
	 * The test validates the proposed algorithms and also measures the time complexity of all algorithms.
	 * 
	 * @param c given an array of load balancing problems.
	 */
	public static void validationTest(loadBalancing[] c){
		
		for(int i=0; i<c.length; i++){
			
			System.out.println("Running Validation Test " + (i+1) + " ...");
			
			// bruteForce
			System.out.println("  Brute-force Method begins...");
			long beginTime_bruteForce = System.currentTimeMillis();
			List<Set<String>> assgn_bruteForce = c[i].getGroupAssignment_bruteForce();
			long totalTime_bruteForce = System.currentTimeMillis() - beginTime_bruteForce;
			
			long groupImpact1_bruteForce = c[i].getGroupImpact(assgn_bruteForce.get(0));
			long groupImpact2_bruteForce = c[i].getGroupImpact(getComplementSet(c[i].groupSet, assgn_bruteForce.get(0)));
			String impact_bruteForce = Long.toString(groupImpact1_bruteForce) + " vs. " + Long.toString(groupImpact2_bruteForce);
			
			int sz_assgn_bruteForce = assgn_bruteForce.size();
			
			List<String> res0 = new ArrayList<String>(4);
			res0.add(0, " ");
			res0.add(1, Integer.toString(sz_assgn_bruteForce));
			res0.add(2, Long.toString(totalTime_bruteForce));
			res0.add(3, impact_bruteForce);
			res_bruteForce.add(res0);
			
			// greedy
			System.out.println("  Greedy Method begins...");
			long beginTime_greedy = System.currentTimeMillis();
			Set<String> assgn_greedy = c[i].getGroupAssignment_greedy();
			long totalTime_greedy = System.currentTimeMillis() - beginTime_greedy;
			
			long groupImpact1_greedy = c[i].getGroupImpact(assgn_greedy);
			long groupImpact2_greedy = c[i].getGroupImpact(getComplementSet(c[i].groupSet, assgn_greedy));
			String impact_greedy = Long.toString(groupImpact1_greedy) + " vs. " + Long.toString(groupImpact2_greedy);
			
			int sz_assgn_greedy = 1;
			
			List<String> res1 = new ArrayList<String>(4);
			if (assgn_bruteForce.contains(assgn_greedy) || assgn_bruteForce.contains(getComplementSet(c[i].groupSet, assgn_greedy))){
				res1.add(0, "Yes, the solution is correct.");
			}else{
				res1.add(0, "No, the solution is not the best.");
			}
			res1.add(1, Integer.toString(sz_assgn_greedy));
			res1.add(2, Long.toString(totalTime_greedy));
			res1.add(3, impact_greedy);
			res_greedy.add(res1);
			
			// greedy search
			System.out.println("  Greedy Search Method begins...");
			long beginTime_greedySearch = System.currentTimeMillis();
			List<Set<String>> assgn_greedySearch = c[i].getGroupAssignment_greedySearch();
			long totalTime_greedySearch = System.currentTimeMillis() - beginTime_greedySearch;
			
			long groupImpact1_greedySearch = c[i].getGroupImpact(assgn_greedySearch.get(0));
			long groupImpact2_greedySearch = c[i].getGroupImpact(getComplementSet(c[i].groupSet, assgn_greedySearch.get(0)));
			String impact_greedySearch = Long.toString(groupImpact1_greedySearch) + " vs. " + Long.toString(groupImpact2_greedySearch);
			
			int sz_assgn_greedySearch = assgn_greedySearch.size();
			List<String> res2 = new ArrayList<String>(4);

			int sum = 0;
			for(int j = 0; j<assgn_greedySearch.size();j++){
				if (assgn_bruteForce.contains(assgn_greedySearch.get(j)) || assgn_bruteForce.contains(getComplementSet(c[i].groupSet, assgn_greedySearch.get(j)))){
					sum++;
				}
			}
			if(sum == sz_assgn_greedySearch && sum == sz_assgn_bruteForce){
				res2.add(0, "Yes, all solutions are here and correct.");
			}else if(sum == sz_assgn_greedySearch && sum != sz_assgn_bruteForce){
				res2.add(0, "No, all solutions are correct, but are not all possible solutions.");
			}else{
				String temp = "No, only " + Integer.toString(sum) + " of them are correct.";
				res2.add(0, temp);
			}
			res2.add(1, Integer.toString(sz_assgn_greedySearch));
			res2.add(2, Long.toString(totalTime_greedySearch));
			res2.add(3, impact_greedySearch);
			res_greedySearch.add(res2);
			
			// full search
			System.out.println("  Full Search Method begins...");
			long beginTime_fullSearch = System.currentTimeMillis();
			List<Set<String>> assgn_fullSearch = c[i].getGroupAssignment_fullSearch();
			long totalTime_fullSearch = System.currentTimeMillis() - beginTime_fullSearch;
			
			long groupImpact1_fullSearch = c[i].getGroupImpact(assgn_fullSearch.get(0));
			long groupImpact2_fullSearch = c[i].getGroupImpact(getComplementSet(c[i].groupSet, assgn_fullSearch.get(0)));
			String impact_fullSearch = Long.toString(groupImpact1_fullSearch) + " vs. " + Long.toString(groupImpact2_fullSearch);
			
			int sz_assgn_fullSearch = assgn_fullSearch.size();
			List<String> res3 = new ArrayList<String>(4);

			int sum2 = 0;
			for(int j = 0; j<assgn_fullSearch.size();j++){
				if (assgn_bruteForce.contains(assgn_fullSearch.get(j)) || assgn_bruteForce.contains(getComplementSet(c[i].groupSet, assgn_fullSearch.get(j)))){
					sum2++;
				}
			}
			if(sum2 == sz_assgn_fullSearch && sum2 == sz_assgn_bruteForce){
				res3.add(0, "Yes, all solutions are here and correct.");
			}else if(sum2 == sz_assgn_fullSearch && sum2 != sz_assgn_bruteForce){
				res3.add(0, "No, all solutions are correct, but are not all possible solutions.");
			}else{
				String temp = "No, only " + Integer.toString(sum2) + " of them are correct.";
				res3.add(0, temp);
			}
			res3.add(1, Integer.toString(sz_assgn_fullSearch));
			res3.add(2, Long.toString(totalTime_fullSearch));
			res3.add(3, impact_fullSearch);
			res_fullSearch.add(res3);
			
		}
	}
	
	/**
	 * This method prints the result of validation test on the screen.
	 */
	public static void showResults(){
		int sz = res_bruteForce.size();
		String[] impact_bruteForce = new String[sz];
		String[] impact_greedy = new String[sz];
		String[] impact_greedySearch = new String[sz];
		String[] validationResults_greedy = new String[sz];
		String[] validationResults_greedySearch = new String[sz];
		String[] validationResults_fullSearch = new String[sz];
		double[] retrievalRate_greedySearch = new double[sz];
		double[] retrievalRate_fullSearch = new double[sz];
		for(int i = 0; i<sz;i++){
			impact_bruteForce[i] =  res_bruteForce.get(i).get(3);
			impact_greedy[i] = res_greedy.get(i).get(3);
			impact_greedySearch[i] = res_greedySearch.get(i).get(3);
			validationResults_greedy[i] = res_greedy.get(i).get(0);
			validationResults_greedySearch[i] = res_greedySearch.get(i).get(0);
			validationResults_fullSearch[i] = res_fullSearch.get(i).get(0);
			retrievalRate_greedySearch[i] = Double.parseDouble(res_greedySearch.get(i).get(1)) / Double.parseDouble(res_bruteForce.get(i).get(1));
			retrievalRate_fullSearch[i] = Double.parseDouble(res_fullSearch.get(i).get(1)) / Double.parseDouble(res_bruteForce.get(i).get(1));
		}
		System.out.println();
		System.out.println("Validation Test Results:");
		for(int i = 0; i < sz; i++){
			System.out.println();
			System.out.println("Test " + Integer.toString(i+1) + " :");
			System.out.println("Baseline Method: ");
			System.out.println("Impacts of assigned groups: " + impact_bruteForce[i]);
			System.out.println("Number of solutions: " + res_bruteForce.get(i).get(1));
			System.out.println("Used time: " + res_bruteForce.get(i).get(2) + "ms");
			System.out.println("Greedy Method: ");
			System.out.println("Impacts of assigned groups: " + impact_greedy[i]);
			System.out.println("Validation: " + validationResults_greedy[i]);
			System.out.println("Used time: " + res_greedy.get(i).get(2) + "ms");
			System.out.println("Greedy Search Method: ");
			System.out.println("Impacts of assigned groups: " + impact_greedySearch[i]);
			System.out.println("Number of solutions: " + res_greedySearch.get(i).get(1));
			System.out.println("Validation: " + validationResults_greedySearch[i]);
			System.out.println("Retrieval Rate: " + retrievalRate_greedySearch[i]);
			System.out.println("Used time: " + res_greedySearch.get(i).get(2) + "ms");
			System.out.println("Full Search Method: ");
			System.out.println("Number of solutions: " + res_fullSearch.get(i).get(1));
			System.out.println("Validation: " + validationResults_fullSearch[i]);
			System.out.println("Retrieval Rate: " + retrievalRate_fullSearch[i]);
			System.out.println("Used time: " + res_fullSearch.get(i).get(2) + "ms");
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		// create problems
		
		graphGenerator generator1 = new graphGenerator("./data/var_dense/DAG_200_0.1_10.txt", "dag");
		graphGenerator generator2 = new graphGenerator("./data/var_dense/DAG_200_0.2_10.txt", "dag");
		graphGenerator generator3 = new graphGenerator("./data/var_dense/DAG_200_0.3_10.txt", "dag");
		graphGenerator generator4 = new graphGenerator("./data/var_dense/DAG_200_0.4_10.txt", "dag");
		graphGenerator generator5 = new graphGenerator("./data/var_dense/DAG_200_0.5_10.txt", "dag");
		graphGenerator generator6 = new graphGenerator("./data/var_dense/DAG_200_0.6_10.txt", "dag");
		graphGenerator generator7 = new graphGenerator("./data/var_dense/DAG_200_0.7_10.txt", "dag");
		graphGenerator generator8 = new graphGenerator("./data/var_dense/DAG_200_0.8_10.txt", "dag");
		graphGenerator generator9 = new graphGenerator("./data/var_dense/DAG_200_0.9_10.txt", "dag");
		graphGenerator generator10 = new graphGenerator("./data/var_dense/DAG_200_1_10.txt", "dag");
		
		loadBalancing[] c = new loadBalancing[10];
		c[0] = new loadBalancing(generator1.graph, "./data/var_dense/source1.txt", "./data/var_dense/destination1.txt", "./data/var_dense/group.txt");
		c[1] = new loadBalancing(generator2.graph, "./data/var_dense/source2.txt", "./data/var_dense/destination.txt", "./data/var_dense/group.txt");
		c[2] = new loadBalancing(generator3.graph, "./data/var_dense/source.txt", "./data/var_dense/destination.txt", "./data/var_dense/group.txt");
		c[3] = new loadBalancing(generator4.graph, "./data/var_dense/source.txt", "./data/var_dense/destination.txt", "./data/var_dense/group.txt");
		c[4] = new loadBalancing(generator5.graph, "./data/var_dense/source.txt", "./data/var_dense/destination.txt", "./data/var_dense/group.txt");
		c[5] = new loadBalancing(generator6.graph, "./data/var_dense/source.txt", "./data/var_dense/destination.txt", "./data/var_dense/group.txt");
		c[6] = new loadBalancing(generator7.graph, "./data/var_dense/source.txt", "./data/var_dense/destination.txt", "./data/var_dense/group.txt");
		c[7] = new loadBalancing(generator8.graph, "./data/var_dense/source.txt", "./data/var_dense/destination.txt", "./data/var_dense/group.txt");
		c[8] = new loadBalancing(generator9.graph, "./data/var_dense/source.txt", "./data/var_dense/destination.txt", "./data/var_dense/group.txt");
		c[9] = new loadBalancing(generator10.graph, "./data/var_dense/source.txt", "./data/var_dense/destination.txt", "./data/var_dense/group.txt");
		
		graphGenerator generator11 = new graphGenerator("./data/var_size/DAG_100_0.4_5.txt", "dag");
		graphGenerator generator12 = new graphGenerator("./data/var_size/DAG_200_0.4_5.txt", "dag");
		graphGenerator generator13 = new graphGenerator("./data/var_size/DAG_300_0.4_5.txt", "dag");
		graphGenerator generator14 = new graphGenerator("./data/var_size/DAG_400_0.4_5.txt", "dag");
		graphGenerator generator15 = new graphGenerator("./data/var_size/DAG_500_0.4_5.txt", "dag");
		graphGenerator generator16 = new graphGenerator("./data/var_size/DAG_600_0.4_5.txt", "dag");
		graphGenerator generator17 = new graphGenerator("./data/var_size/DAG_700_0.4_5.txt", "dag");
		graphGenerator generator18 = new graphGenerator("./data/var_size/DAG_800_0.4_5.txt", "dag");
		graphGenerator generator19 = new graphGenerator("./data/var_size/DAG_900_0.4_5.txt", "dag");
		graphGenerator generator20 = new graphGenerator("./data/var_size/DAG_1000_0.4_5.txt", "dag");
		
		loadBalancing[] d = new loadBalancing[10];
		d[0] = new loadBalancing(generator11.graph, "./data/var_size/source100.txt", "./data/var_size/destination100.txt", "./data/var_size/group100.txt");
		d[1] = new loadBalancing(generator12.graph, "./data/var_size/source.txt", "./data/var_size/destination200.txt", "./data/var_size/group200.txt");
		d[2] = new loadBalancing(generator13.graph, "./data/var_size/source.txt", "./data/var_size/destination300.txt", "./data/var_size/group300.txt");
		d[3] = new loadBalancing(generator14.graph, "./data/var_size/source.txt", "./data/var_size/destination400.txt", "./data/var_size/group400.txt");
		d[4] = new loadBalancing(generator15.graph, "./data/var_size/source.txt", "./data/var_size/destination500.txt", "./data/var_size/group500.txt");
		d[5] = new loadBalancing(generator16.graph, "./data/var_size/source.txt", "./data/var_size/destination600.txt", "./data/var_size/group600.txt");
		d[6] = new loadBalancing(generator17.graph, "./data/var_size/source700.txt", "./data/var_size/destination700.txt", "./data/var_size/group700.txt");
		d[7] = new loadBalancing(generator18.graph, "./data/var_size/source800.txt", "./data/var_size/destination800.txt", "./data/var_size/group800.txt");
		d[8] = new loadBalancing(generator19.graph, "./data/var_size/source900.txt", "./data/var_size/destination900.txt", "./data/var_size/group900.txt");
		d[9] = new loadBalancing(generator20.graph, "./data/var_size/source1000.txt", "./data/var_size/destination1000.txt", "./data/var_size/group1000.txt");
		
		graphGenerator generator = new graphGenerator("./data/DAG_50_0.4_5.txt", "dag");
		loadBalancing[] t = new loadBalancing[10];
		t[0] = new loadBalancing(generator.graph, "./data/source50.txt", "./data/destination50.txt", "./data/group50_4.txt");
		t[1] = new loadBalancing(generator.graph, "./data/source50.txt", "./data/destination50.txt", "./data/group50_5.txt");
		t[2] = new loadBalancing(generator.graph, "./data/source50.txt", "./data/destination50.txt", "./data/group50_6.txt");
		t[3] = new loadBalancing(generator.graph, "./data/source50.txt", "./data/destination50.txt", "./data/group50_7.txt");
		t[4] = new loadBalancing(generator.graph, "./data/source50.txt", "./data/destination50.txt", "./data/group50_8.txt");
		t[5] = new loadBalancing(generator.graph, "./data/source50.txt", "./data/destination50.txt", "./data/group50_9.txt");
		t[6] = new loadBalancing(generator.graph, "./data/source50.txt", "./data/destination50.txt", "./data/group50_10.txt");
		t[7] = new loadBalancing(generator.graph, "./data/source50.txt", "./data/destination50.txt", "./data/group50_11.txt");
		t[8] = new loadBalancing(generator.graph, "./data/source50.txt", "./data/destination50.txt", "./data/group50_12.txt");
		t[9] = new loadBalancing(generator.graph, "./data/source50.txt", "./data/destination50.txt", "./data/group50_13.txt");
		
		// tests and results
		
		validationTest(c);
	
		showResults();// show first 10
		
		validationTest(t);
		
		showResults();// show all 20
		
		validationTest(d);
		
		showResults();// show all 20

		
		// baseline method
//		long beginTime0 = System.currentTimeMillis();
//		List<Set<String>> assgn0 = a.getGroupAssignment_baseLine();
//		long totalTime0 = System.currentTimeMillis() - beginTime0;
//		System.out.println("Total used time for baseline method: " + totalTime0 + "ms");
//		// for display
//		System.out.println("Number of possible Assignment: " + assgn0.size());
//		for (int i = 0; i < assgn0.size(); i++) {
//			Set<String> groupNodes = new HashSet<String>(a.groupSet);
//			System.out.println("Assignment " + (i+1) + ": ");
//			System.out.println("Set " + assgn0.get(i).toString() + " Impact: " + a.getGroupImpact(assgn0.get(i)) + " ");
//			groupNodes.removeAll(assgn0.get(i));
//			System.out.println("The other set: " + groupNodes + " Impact: " + a.getGroupImpact(groupNodes));
//			System.out.println();
//		}
		
		
		//visualize graph
//		DOTExporter exporter = new DOTExporter();
//		String targetDirectory = "results/graph/";
//		new File(targetDirectory).mkdirs();
//		exporter.export(new FileWriter(targetDirectory + "evaluation_graph_default.dot"), c.evaluationGraph);

	}

}
