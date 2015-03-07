package org.clueminer.graph.adjacencyMatrix;

import java.util.Collection;
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
    public void testIterables() {
        buildSimpleGraph();
        Collection<Node> nodes = g.getNodes().toCollection();
        assertEquals(3, nodes.size());

        nodes = g.getNeighbors(n3).toCollection();
        assertEquals(1, nodes.size());

        nodes = g.getNeighbors(n1).toCollection();
        assertEquals(1, nodes.size());

        nodes = g.getNeighbors(n2).toCollection();
        assertEquals(2, nodes.size());

        Collection<Edge> edges = g.getEdges().toCollection();
        assertEquals(2, edges.size());

        edges = g.getEdges(n1).toCollection();
        assertEquals(1, edges.size());

        edges = g.getEdges(n2).toCollection();
        assertEquals(2, edges.size());

        edges = g.getEdges(n3).toCollection();
        assertEquals(1, edges.size());

    }

    private void buildSimpleGraph() {
        f = AdjMatrixFactory.getInstance();
        double[] coordinates = {2, 1};
        n1 = (AdjMatrixNode) f.newNode(coordinates);
        n2 = (AdjMatrixNode) f.newNode(2);
        n3 = (AdjMatrixNode) f.newNode(2);
        e1 = (AdjMatrixEdge) f.newEdge(n1, n2, 1, 2, false);
        e2 = (AdjMatrixEdge) f.newEdge(n3, n2, 1, 3, false);
        g = new AdjMatrixGraph(3);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addEdge(e1);
        g.addEdge(e2);
    }

    @Test
    public void buildGraphTest() {
        f = AdjMatrixFactory.getInstance();
        double[] coordinates = {2, 1};
        n1 = (AdjMatrixNode) f.newNode(coordinates);
        n2 = (AdjMatrixNode) f.newNode(2);
        n3 = (AdjMatrixNode) f.newNode(2);
        Node n4 = (AdjMatrixNode) f.newNode();
        e1 = (AdjMatrixEdge) f.newEdge(n1, n2, 1, 2, false);
        e2 = (AdjMatrixEdge) f.newEdge(n3, n2, 1, 3, false);
        g = new AdjMatrixGraph(3);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addEdge(e1);
        g.addEdge(e2);
        assertEquals(0, g.getIndex(n1));
        assertEquals(1, g.getIndex(n2));
        assertEquals(2, g.getIndex(n3));
        assertEquals(2, g.getEdge(n1, n2).getWeight(), 0.0001);
        assertEquals(3, g.getEdge(n2, n3).getWeight(), 0.0001);
        assertEquals(true, g.contains(n1));
        assertEquals(false, g.contains(n4));
        assertEquals(true, g.contains(e1));
        assertEquals(1, g.getDegree(n1));
        assertEquals(2, g.getDegree(n2));
    }

    /*  @Test
     public void printGraph() {
     buildSimpleGraph();
     System.out.println(g.graphVizExport());
     }*/
}
