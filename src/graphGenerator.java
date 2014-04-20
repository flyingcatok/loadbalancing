
import java.util.*;
import java.io.*;

import org.jgrapht.*;
import org.jgrapht.experimental.dag.*;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.jgrapht.graph.*;

public class graphGenerator {
	
	Graph<String, DefaultEdge> graph;
	
	public graphGenerator(String fileAddr, String flag) throws IOException, CycleFoundException{
		if(flag == "dag"){
			graph = createDAG(fileAddr);
		}else if(flag == "udg"){
			graph = createUndirectedGraph(fileAddr);
		}else{
			throw new Error("Data flag error!");
		}
	}
	
	public graphGenerator() throws CycleFoundException{
		graph = createDAG();
	}
	
	private static DirectedAcyclicGraph<String, DefaultEdge> createDAG() throws CycleFoundException{
		DirectedAcyclicGraph<String, DefaultEdge> dg =
	            new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);
		
		// add the vertices
		dg.addVertex("S1");
		dg.addVertex("S2");
		dg.addVertex("S3");
		dg.addVertex("A");
		dg.addVertex("B");
		dg.addVertex("C");
		dg.addVertex("D");
		dg.addVertex("E");
		dg.addVertex("F");
		dg.addVertex("T");
		
		// add edges 
		dg.addDagEdge("S1", "A");
		dg.addDagEdge("S1", "B");
		dg.addDagEdge("S2", "A");
		dg.addDagEdge("S3", "A");
		dg.addDagEdge("S3", "C");
		dg.addDagEdge("A", "B");
		dg.addDagEdge("A", "C");
		dg.addDagEdge("B", "D");
		dg.addDagEdge("B", "E");
		dg.addDagEdge("C", "D");
		dg.addDagEdge("C", "F");
		dg.addDagEdge("D", "T");
		dg.addDagEdge("E", "T");
		dg.addDagEdge("F", "T");

		return dg;
		
	}

	private static DirectedAcyclicGraph<String, DefaultEdge> createDAG(String addr) throws IOException, CycleFoundException{
		DirectedAcyclicGraph<String, DefaultEdge> dg =
	            new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);
		
		File fin = new File(addr);
		
		// Construct BufferedReader from FileReader
		BufferedReader br = new BufferedReader(new FileReader(fin));
	 
		// parse data file
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		ArrayList<String> sourceVertices = new ArrayList<String>();
		ArrayList<String> targetVertices = new ArrayList<String>();
		
		String line = null;
		
		while ((line = br.readLine()) != null) {
			String[] vertices = line.split("\t");

			if (!vertices[0].isEmpty()){
				if (Integer.parseInt(vertices[0]) < Integer.parseInt(vertices[1])) {
					sourceVertices.add(vertices[0]);
					targetVertices.add(vertices[1]);
					if (max < Integer.parseInt(vertices[1])){
						max = Integer.parseInt(vertices[1]);
					}
					if (min > Integer.parseInt(vertices[0])){
						min = Integer.parseInt(vertices[0]);
					}
				}
			}

		}
	 
		br.close();
		
		// add vertices and edges
		for(int i = min; i < max + 1; i++){
			dg.addVertex(Integer.toString(i));
		}
		for(int i = 0; i < sourceVertices.size(); i++){
			dg.addDagEdge(sourceVertices.get(i), targetVertices.get(i));
		}
		
		return dg;
	}
	
	private static UndirectedGraph<String, DefaultEdge> createUndirectedGraph(String addr) throws IOException{
		UndirectedGraph<String, DefaultEdge> udg =
	            new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		
		File fin = new File(addr);
		
		// Construct BufferedReader from FileReader
		BufferedReader br = new BufferedReader(new FileReader(fin));
	 
		// parse data file
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		ArrayList<String> sourceVertices = new ArrayList<String>();
		ArrayList<String> targetVertices = new ArrayList<String>();
		
		String line = null;
		
		while ((line = br.readLine()) != null) {
			String[] vertices = line.split("\t");

			sourceVertices.add(vertices[0]);
			targetVertices.add(vertices[1]);
			if (max < Integer.parseInt(vertices[1])){
				max = Integer.parseInt(vertices[1]);
			}
			if (min > Integer.parseInt(vertices[0])){
				min = Integer.parseInt(vertices[0]);
			}

		}
	 
		br.close();
		
		// add vertices and edges
		for(int i = min; i < max + 1; i++){
			udg.addVertex(Integer.toString(i));
		}
		for(int i = 0; i < sourceVertices.size(); i++){
			udg.addEdge(sourceVertices.get(i), targetVertices.get(i));
		}
		
		return udg;
		
	}

}
