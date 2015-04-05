package org.clueminer.graph.adjacencyList;

import java.util.ArrayList;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Node;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class AdjListGraphTest {

	AdjListFactory factory = AdjListFactory.getInstance();
	static AdjListGraph graph = new AdjListGraph();
	static ArrayList<Node> nodes = new ArrayList<>();
	static ArrayList<Edge> edges = new ArrayList<>();
	static boolean initialized = false;

	public AdjListGraphTest() {
		if(initialized)
			return;
		initialized = true;
		nodes.add(factory.newNode());
		nodes.add(factory.newNode());
		nodes.add(factory.newNode());

		edges.add(factory.newEdge(nodes.get(0), nodes.get(1)));
		edges.add(factory.newEdge(nodes.get(1), nodes.get(2)));
	}

	@Before
	public void reset() {
		graph.clear();
		graph.addAllNodes(nodes);
		graph.addAllEdges(edges);
	}

	@Test
	public void removeNodeTest() {
		System.out.println("Remove Node Test");
		graph.removeNode(nodes.get(0));
		graph.print();
	}

	@Test
	public void removeEdgeTest() {
		System.out.println("Remove Edge Test");
		graph.removeEdge(edges.get(0));
		graph.print();
	}

	@Test
	public void buildGraphTest() {
		System.out.println("Build Graph Test");
		graph.print();
	}
}
