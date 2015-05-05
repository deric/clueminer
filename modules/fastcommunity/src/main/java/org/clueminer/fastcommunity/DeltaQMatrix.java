package org.clueminer.fastcommunity;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import org.clueminer.clustering.aggl.Element;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Hamster
 */
public class DeltaQMatrix {

	Map<IntPair, Element> matrix = new HashMap<>();
	PriorityQueue<Element> pQ;

	DeltaQMatrix(PriorityQueue<Element> pq) {
		this.pQ = pq;
	}

	void build(AdjListGraph graph, double[] a) {
		for(Node node : graph.getNodes()) {
			for(Node neighbor : graph.getNeighbors(node)) {
				int i = (int) node.getId();
				int j = (int) neighbor.getId();
				double delta = 2 * (FastCommunity.INITIAL_EIJ - (a[i] * a[j]));
				if(i < j)
					matrix.put(new IntPair(i, j), new Element(delta, i, j));
				else
					matrix.put(new IntPair(j, i), new Element(delta, j, i));
			}
		}
	}

	Element get(Integer i, Integer j) {
		return matrix.get(IntPair.ordered(i, j));
	}

	void add(Integer i, Integer j, Double value) {
		Element element;
		if(i < j)
			element = new Element(value, i, j);
		else
			element = new Element(value, j, i);
		matrix.put(IntPair.ordered(i, j), element);
		pQ.add(element);
	}

	void remove(Integer i, Integer j) {
		Element deleted = matrix.remove(IntPair.ordered(i, j));
		if(deleted != null)
			pQ.remove(deleted);
	}

}
