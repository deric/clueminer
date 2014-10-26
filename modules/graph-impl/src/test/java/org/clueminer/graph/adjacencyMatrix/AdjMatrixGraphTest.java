package org.clueminer.graph.adjacencyMatrix;

import java.util.Iterator;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Node;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class AdjMatrixGraphTest {

    AdjMatrixFactory f;
    AdjMatrixNode n1;
    AdjMatrixNode n2;
    AdjMatrixNode n3;
    AdjMatrixEdge e1;
    AdjMatrixEdge e2;
    AdjMatrixGraph g;

    @Test
    public void testNewEdge() {
        buildSimpleGraph();
        Edge a = g.getEdge(n1, n2);
        assertEquals(2.0, a.getWeight(), 0.000001);
        a = g.getEdge(g.getNode(1), g.getNode("aa"));
        assertEquals(3.0, a.getWeight(), 0.000001);
        assertEquals(g.getNode("aa").getId(), "aa");
    }

    @Test
    public void testIterables() {
        buildSimpleGraph();
        Iterator<Node> it = g.getNodes().iterator();
        assertEquals(0, it.next().getId());
        assertEquals(1, it.next().getId());
        assertEquals("aa", it.next().getId());
        assertEquals(false, it.hasNext());

        it = g.getNeighbors(n3).iterator();
        assertEquals(1, it.next().getId());
        assertEquals(false, it.hasNext());

        it = g.getNeighbors(n1).iterator();
        assertEquals(1, it.next().getId());
        assertEquals(false, it.hasNext());

        it = g.getNeighbors(n2).iterator();
        assertEquals(0, it.next().getId());
        assertEquals("aa", it.next().getId());
        assertEquals(false, it.hasNext());

    }

    private void buildSimpleGraph() {
        f = AdjMatrixFactory.getInstance();
        n1 = (AdjMatrixNode) f.newNode(0);
        n2 = (AdjMatrixNode) f.newNode(1);
        n3 = (AdjMatrixNode) f.newNode("aa");
        e1 = (AdjMatrixEdge) f.newEdge(n1, n2, 1, 2, false);
        e2 = (AdjMatrixEdge) f.newEdge(n3, n2, 1, 3, false);
        g = new AdjMatrixGraph(3);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addEdge(e1);
        g.addEdge(e2);
    }
}
