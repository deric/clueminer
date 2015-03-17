package org.clueminer.graph.adjacencyList;

import java.util.ArrayList;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Node;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class AdjListGraphTest {

	@Test
	public void removeNodeTest() {
		System.out.println("Remove Node Test");
		AdjListFactory factory = AdjListFactory.getInstance();
		AdjListGraph graph = new AdjListGraph();

		ArrayList<Node> nodes = new ArrayList<>();
		nodes.add(factory.newNode());
		nodes.add(factory.newNode());
		nodes.add(factory.newNode());
		graph.addAllNodes(nodes);

		ArrayList<Edge> edges = new ArrayList<>();
		edges.add(factory.newEdge(nodes.get(0), nodes.get(1)));
		edges.add(factory.newEdge(nodes.get(1), nodes.get(2)));
		graph.addAllEdges(edges);

		graph.removeNode(nodes.get(0));

		graph.print();
	}

	@Test
	public void removeEdgeTest() {
		System.out.println("Remove Edge Test");
		AdjListFactory factory = AdjListFactory.getInstance();
		AdjListGraph graph = new AdjListGraph();

		ArrayList<Node> nodes = new ArrayList<>();
		nodes.add(factory.newNode());
		nodes.add(factory.newNode());
		nodes.add(factory.newNode());
		graph.addAllNodes(nodes);

		ArrayList<Edge> edges = new ArrayList<>();
		edges.add(factory.newEdge(nodes.get(0), nodes.get(1)));
		edges.add(factory.newEdge(nodes.get(1), nodes.get(2)));
		graph.addAllEdges(edges);

		graph.removeEdge(edges.get(0));

		graph.print();
	}

	@Test
	public void buildGraphTest() {
		System.out.println("Build Graph Test");
		AdjListFactory factory = AdjListFactory.getInstance();
		AdjListGraph graph = new AdjListGraph();

		ArrayList<Node> nodes = new ArrayList<>();
		nodes.add(factory.newNode());
		nodes.add(factory.newNode());
		nodes.add(factory.newNode());
		graph.addAllNodes(nodes);

		ArrayList<Edge> edges = new ArrayList<>();
		edges.add(factory.newEdge(nodes.get(0), nodes.get(1)));
		edges.add(factory.newEdge(nodes.get(1), nodes.get(2)));
		graph.addAllEdges(edges);

		graph.print();
	}
}
