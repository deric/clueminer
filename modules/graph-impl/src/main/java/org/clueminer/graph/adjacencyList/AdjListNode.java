package org.clueminer.graph.adjacencyList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.api.NodeIterable;

/**
 *
 * @author Hamster
 */
public class AdjListNode implements Node {

	private final long id;
	Object label;
	HashMap<Long, AdjListEdge> edges;

	public AdjListNode(long id) {
		this.id = id;
	}

	public AdjListNode(long id, Object label) {
		this.label = label;
		this.id = id;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public Object getLabel() {
		return label;
	}

	public void addEdge(Edge edge) {
		edges.put(edge.getId(), (AdjListEdge) edge);
	}

	public Edge getEdge(Node neighbor) {
		for (Map.Entry<Long, AdjListEdge> entrySet : edges.entrySet()) {
			Edge edge = entrySet.getValue();
			if (edge.getSource() == neighbor || edge.getTarget() == neighbor) {
				return edge;
			}
		}
		return null;
	}

	NodeIterable getNeighbors() {
		List<AdjListNode> neighbors = new LinkedList();
		for (Map.Entry<Long, AdjListEdge> entrySet : edges.entrySet()) {
			AdjListEdge edge = entrySet.getValue();
			if (edge.getSource() != this) {
				neighbors.add(edge.getSource());
			}
			if (edge.getTarget() != this) {
				neighbors.add(edge.getTarget());
			}
		}
		return new AdjListNodeIterable(neighbors);
	}
	
	EdgeIterable getEdges() {
		return new AdjListEdgeIterable(edges);
	}

	int getDegree() {
		return edges.size();
	}

	boolean isAdjacent(Node node) {
		for (Map.Entry<Long, AdjListEdge> entrySet : edges.entrySet()) {
			AdjListEdge edge = entrySet.getValue();
			if(edge.getSource() == node || edge.getTarget() == node)
				return true;
		}
		return false;
	}

}
