package org.clueminer.graph.adjacencyList;

import java.util.ArrayList;
import java.util.Collection;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class AdjListGraphTest {

    private static final AdjListFactory factory = AdjListFactory.getInstance();
    private static final AdjListGraph graph = new AdjListGraph();
    private static final ArrayList<Node> nodes = new ArrayList<>();
    private static final ArrayList<Edge> edges = new ArrayList<>();
    private Node n1;
    private Node n2;
    private Node n3;
    private Edge e1;
    private Edge e2;
    private Graph g;
    private static final double DELTA = 1e-4;


    @BeforeClass
    public static void setUpClass() {
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
    public void nodeTest() {
        assertEquals(3, nodes.size());
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

    @Test
    public void testIterables() {
        buildSimpleGraph();
        Collection<Node> myNodes = g.getNodes().toCollection();
        assertEquals(3, myNodes.size());

        myNodes = g.getNeighbors(n3).toCollection();
        assertEquals(1, myNodes.size());

        myNodes = g.getNeighbors(n1).toCollection();
        assertEquals(1, myNodes.size());

        myNodes = g.getNeighbors(n2).toCollection();
        assertEquals(2, myNodes.size());

        Collection<Edge> myEdges = g.getEdges().toCollection();
        assertEquals(2, myEdges.size());

        myEdges = g.getEdges(n1).toCollection();
        assertEquals(1, myEdges.size());

        myEdges = g.getEdges(n2).toCollection();
        assertEquals(2, myEdges.size());

        myEdges = g.getEdges(n3).toCollection();
        assertEquals(1, myEdges.size());

    }

    private void buildSimpleGraph() {
        double[] coordinates = {2, 1};
        n1 = factory.newNode(coordinates);
        n2 = factory.newNode(2);
        n3 = factory.newNode(2);
        e1 = factory.newEdge(n1, n2, 1, 2, false);
        e2 = factory.newEdge(n3, n2, 1, 3, false);
        g = new AdjListGraph();
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addEdge(e1);
        g.addEdge(e2);
    }

    @Test
    public void buildGraphTest2() {
        double[] coordinates = {2, 1};
        n1 = factory.newNode(coordinates);
        n2 = factory.newNode(2);
        n3 = factory.newNode(2);
        Node n4 = factory.newNode();
        e1 = factory.newEdge(n1, n2, 1, 2, false);
        e2 = factory.newEdge(n3, n2, 1, 3, false);
        g = new AdjListGraph();
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addEdge(e1);
        g.addEdge(e2);
        assertEquals(0, g.getIndex(n1));
        assertEquals(1, g.getIndex(n2));
        assertEquals(2, g.getIndex(n3));
        assertEquals(2, g.getEdge(n1, n2).getWeight(), DELTA);
        assertEquals(3, g.getEdge(n2, n3).getWeight(), DELTA);
        assertEquals(true, g.contains(n1));
        assertEquals(false, g.contains(n4));
        assertEquals(true, g.contains(e1));
        assertEquals(1, g.getDegree(n1));
        assertEquals(2, g.getDegree(n2));
    }

}
