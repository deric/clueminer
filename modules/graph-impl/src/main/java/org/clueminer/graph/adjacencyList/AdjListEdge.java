package org.clueminer.graph.adjacencyList;

import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Hamster
 */
public class AdjListEdge implements Edge {

	private final long id;
	private final AdjListNode source;
	private final AdjListNode target;
	private boolean directed;
	private final double weight;

	AdjListEdge(long id, Node source, Node target, double weight) {
		this.id = id;
		this.source = (AdjListNode) source;
		this.target = (AdjListNode) target;
		this.weight = weight;
	}

	@Override
	public boolean isDirected() {
		return directed;
	}

	@Override
	public AdjListNode getSource() {
		return source;
	}

	@Override
	public AdjListNode getTarget() {
		return target;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public Object getLabel() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
