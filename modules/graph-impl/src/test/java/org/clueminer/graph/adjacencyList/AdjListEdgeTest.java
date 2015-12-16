package org.clueminer.graph.adjacencyList;

import org.clueminer.graph.api.Node;
import org.clueminer.graph.impl.Commons;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class AdjListEdgeTest {

    private static final double DELTA = 1e-9;

    @Test
    public void completeConstructorTest() {
        long id = 5;
        AdjListNode source = new AdjListNode(1);
        AdjListNode target = new AdjListNode(2);
        double weight = 2.5;
        boolean directed = true;
        AdjListEdge edge = new AdjListEdge(id, source, target, directed, weight);
        assertEquals(id, edge.getId());
        assertSame(source, edge.getSource());
        assertSame(target, edge.getTarget());
        assertEquals(directed, edge.isDirected());
        assertEquals(weight, edge.getWeight(), DELTA);
    }

    @Test
    public void defaultConstructorTest() {
        long id = 5;
        AdjListNode source = new AdjListNode(1);
        AdjListNode target = new AdjListNode(2);
        AdjListEdge edge = new AdjListEdge(id, source, target);
        assertEquals(id, edge.getId());
        assertSame(source, edge.getSource());
        assertSame(target, edge.getTarget());
        assertFalse(edge.isDirected());
        assertEquals(1, edge.getWeight(), DELTA);
    }

    @Test
    public void testWeightSetter() {
        long id = 5;
        AdjListNode source = new AdjListNode(1);
        AdjListNode target = new AdjListNode(2);
        double weight = 10.0;
        AdjListEdge edge = new AdjListEdge(id, source, target, false, weight);
        assertEquals(weight, edge.getWeight(), DELTA);
        edge.setWeight(-5.0);
        assertEquals(-5.0, edge.getWeight(), DELTA);
    }

    @Test
    public void testAttributes() {
        AdjListGraph graph = new AdjListGraph();
        Commons.buildSmallGraph(graph, AdjListFactory.getInstance());

        assertEquals(7, graph.getNodeCount());
        int nodeCnt = 0;
        long lastId = 0;
        for (Node n : graph.getNodes()) {
            n.setAttribute("visited", true);
            lastId = n.getId();
            nodeCnt++;
        }
        assertEquals(7, nodeCnt);
        assertEquals(true, graph.getNode(lastId).getAttribute("visited"));
    }
}
