package org.clueminer.fastcommunity;

import java.util.HashMap;
import java.util.Map;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Hamster
 */
public class DeltaQMatrix {

	Map<IntPair, Double> matrix = new HashMap<>();

	void build(AdjMatrixGraph graph, double[] a) {
		for(Node node : graph.getNodes()) {
			for(Node neighbor : graph.getNeighbors(node)) {
				int i = (int) node.getId();
				int j = (int) neighbor.getId();
				double delta = 2 * (FastCommunity.INITIAL_EIJ - (a[i] * a[j]));
				if(i < j)
					matrix.put(new IntPair(i, j), delta);
				else
					matrix.put(new IntPair(j, i), delta);
			}
		}
	}

	Double get(Integer i, Integer j) {
		if(i < j)
			return matrix.get(new IntPair(i, j));
		return matrix.get(new IntPair(j, i));
	}

	void set(Integer i, Integer j, Double value) {
		if(i < j)
			matrix.put(new IntPair(i, j), value);
		else
			matrix.put(new IntPair(j, i), value);
	}

}
