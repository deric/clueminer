package org.clueminer.graph.adjacencyList;

import java.util.ArrayList;
import org.clueminer.graph.api.Edge;
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
}
