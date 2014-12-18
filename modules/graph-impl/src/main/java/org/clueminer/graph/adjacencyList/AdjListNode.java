package org.clueminer.graph.adjacencyList;

import org.clueminer.graph.api.Node;

/**
 *
 * @author Hamster
 */
public class AdjListNode implements Node {

	private final long id;
	Object label;

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

}
