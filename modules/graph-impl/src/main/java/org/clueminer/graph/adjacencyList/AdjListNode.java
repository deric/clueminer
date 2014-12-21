package org.clueminer.graph.adjacencyList;

import java.util.HashMap;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Hamster
 */
public class AdjListNode implements Node {

	private final long id;
	Object label;
	HashMap<Long, Edge> edgesIn;
	HashMap<Long, Edge> edgesOut;

	public AdjListNode(long id) {
		this.id = id;
	}

	public AdjListNode(long id, Object label) {
		this.label = label;
		this.id = id;
	}
	
	public void addEdgeIn(Edge edge) {
		edgesIn.put(edge.getId(), edge);
		if(!edge.isDirected())
			edgesOut.put(edge.getId(), edge);
	}

	public void addEdgeOut(Edge edge) {
		edgesOut.put(edge.getId(), edge);
		if(!edge.isDirected())
			edgesIn.put(edge.getId(), edge);
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
