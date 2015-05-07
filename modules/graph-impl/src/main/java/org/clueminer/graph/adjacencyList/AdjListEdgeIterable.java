package org.clueminer.graph.adjacencyList;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;

/**
 *
 * @author Hamster
 */
public class AdjListEdgeIterable implements EdgeIterable {

	private final List<AdjListEdge> edges;

	public AdjListEdgeIterable(Map<Long, AdjListEdge> edges) {
		this.edges = new LinkedList<>(edges.values());
	}

	public AdjListEdgeIterable(List<AdjListEdge> edges) {
		this.edges = new LinkedList<>(edges);
	}

	@Override
	public Iterator<Edge> iterator() {
		Iterator<Edge> iterator = new Iterator<Edge>() {

			private final Iterator<AdjListEdge> it = edges.iterator();

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public Edge next() {
				return it.next();
			}

			@Override
			public void remove() {
				it.remove();
			}
		};
		return iterator;
	}

	@Override
	public Edge[] toArray() {
		Edge[] array = new Edge[edges.size()];
		for(int i = 0; i < edges.size(); i++) {
			array[i] = edges.get(i);
		}
		return array;
	}

	@Override
	public Collection<Edge> toCollection() {
		return new LinkedList<Edge>(edges);
	}

	@Override
	public void doBreak() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
