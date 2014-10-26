package org.clueminer.graph.adjacencyMatrix;

import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Node;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class AdjMatrixGraphTest {

    @Test
    public void testNewEdge() {
        AdjMatrixFactory f = AdjMatrixFactory.getInstance();
        AdjMatrixNode n1 = (AdjMatrixNode) f.newNode(0);
        AdjMatrixNode n2 = (AdjMatrixNode) f.newNode(1);
        AdjMatrixNode n3 = (AdjMatrixNode) f.newNode("aa");
        AdjMatrixEdge e1 = (AdjMatrixEdge) f.newEdge(n1, n2, 1, 2, false);
        AdjMatrixEdge e2 = (AdjMatrixEdge) f.newEdge(n3, n2, 1, 3, false);
        AdjMatrixGraph g = new AdjMatrixGraph(3);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addEdge(e1);
        g.addEdge(e2);
        System.out.println(e2.getSource().getNumber());
        Edge a =  g.getEdge(n1,n2);
        assertEquals(2.0, a.getWeight(), 0.000001);
        a = g.getEdge(g.getNode(1),g.getNode("aa"));
        assertEquals(3.0, a.getWeight(), 0.000001);
        assertEquals(g.getNode("aa").getId(), "aa");
    }
}
