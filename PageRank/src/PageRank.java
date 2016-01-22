import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class PageRank {

	public static void main(String[] args) throws IOException {
		long startTime = System.nanoTime();
		// The number of edges is given, which is 510539.
		int[][] g = readGraph("web-Google.txt", 5105039);
		long endTime = System.nanoTime();
		double duration = (endTime - startTime)/1000000000.0; // seconds
		System.out.println("Read File: " + duration + " seconds");
		
		startTime = System.nanoTime();
		// Let random teleporting probability = 0.2; beta = 0.8
		Map<Integer, Double> m = pageRank(g, 0.8, 0.000001);
		endTime = System.nanoTime();
		duration = (endTime - startTime)/1000000000.0; // seconds
		System.out.println("Page Rank Computation time: " + duration + " seconds");
		// Print the PageRank value of nodes.
		System.out.println("Node 99: " + m.get(99));
		System.out.println("Node 97: " + m.get(97));
		System.out.println("Node 101: " + m.get(101));
		System.out.println("Node 20: " + m.get(20));
		System.out.println("Node 33: " + m.get(33));
	}
	
	private static int[][] readGraph(String file, int edges) throws IOException{
		int[][] g = new int[edges][2];
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			// Ignore the first 4 lines.
			br.readLine();
			br.readLine();
			br.readLine();
			br.readLine();
			String line = br.readLine();
			int from, to, sep;
			int count = 0;
			while(line!=null){
				sep = line.indexOf("\t");
				from = Integer.parseInt(line.substring(0, sep));
				to = Integer.parseInt(line.substring(sep+1));
				g[count][0] = from;
				g[count++][1] = to;
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return g;
	}
	
	// Return PageRank of nodes
	private static Map<Integer, Double> pageRank(int[][] g, double beta, double eps){
		// r_i: PageRank of node i
		// d_i: out-degree of node i
		// r_new_i: the temporary PageRank of node i during iteration
		Map<Integer, Double> r = new HashMap<Integer, Double>();		
		Map<Integer, Integer> d = new HashMap<Integer, Integer>();		
		Map<Integer, Double> r_new = new HashMap<Integer, Double>();	

		int e = g.length;
		// The number of nodes is given, which is 875713.
		Set<Integer> nodeSet = new HashSet<Integer>(875713);
		for (int i=0; i<e; i++){
			nodeSet.add(g[i][0]);
			nodeSet.add(g[i][1]);
		}
		int n = nodeSet.size();
		System.out.println("n: " + nodeSet.size());
		// n equals to nodeSet.size()
		
		// Initialize the value of r, d, r_new
		for (Integer s : nodeSet) {
		    r.put(s, 1.0/n);
		    r_new.put(s, 0.0);
		    d.put(s, 0);
		}		
		System.out.println("Initialization is done.");
		
		// Compute d
		for (int i=0; i<e; i++){
			d.put(g[i][0], d.get(g[i][0])+1);
		}

		System.out.println("Computation of d is done.");
		int t = 1;
		double sumDiff = 0.0;
		Double tt, sum_r_new = 0.0, value;

		do {
			for (int i=0; i<e; i++){
				tt = r_new.get(g[i][1]); //toNode
				r_new.put(g[i][1], tt+beta*r.get(g[i][0])/d.get(g[i][0])); 
			}
			
			sum_r_new = 0.0;
			for (Double f : r_new.values()) {
			    sum_r_new += f;
			}
			
			// add each r with leak (evenly distribute) and compute sumDiff and clear r_new
			sumDiff = 0.0;
			for (Integer s : nodeSet) {
				value = r_new.get(s) + (1.0-sum_r_new)/n;
				sumDiff += Math.abs(value-r.get(s));
			    r.put(s, value);
			    r_new.put(s, 0.0);
			}
			System.out.println("Iter: " + t++);
		} while (sumDiff > eps);
		
		return r;
	}

}
