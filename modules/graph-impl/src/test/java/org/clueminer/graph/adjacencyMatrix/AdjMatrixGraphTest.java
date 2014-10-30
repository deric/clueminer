package org.clueminer.graph.adjacencyMatrix;

import java.util.Collection;
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
        Iterator<Node> itN = g.getNodes().iterator();
        assertEquals(0, itN.next().getId());
        assertEquals(1, itN.next().getId());
        assertEquals("aa", itN.next().getId());
        assertEquals(false, itN.hasNext());

        itN = g.getNeighbors(n3).iterator();
        assertEquals(1, itN.next().getId());
        assertEquals(false, itN.hasNext());

        itN = g.getNeighbors(n1).iterator();
        assertEquals(1, itN.next().getId());
        assertEquals(false, itN.hasNext());

        itN = g.getNeighbors(n2).iterator();
        assertEquals(0, itN.next().getId());
        assertEquals("aa", itN.next().getId());
        assertEquals(false, itN.hasNext());
        
        Collection<Edge> colE = g.getEdges().toCollection();
        assertEquals(2,colE.size());
        
        colE = g.getEdges(n1).toCollection();
        assertEquals(1,colE.size());
        colE = g.getEdges(n2).toCollection();
        assertEquals(2,colE.size());
        colE = g.getEdges(n3).toCollection();
        assertEquals(1,colE.size());
        

    }

    private void buildSimpleGraph() {
        f = AdjMatrixFactory.getInstance();
        double[] coordinates = {2, 1};
        n1 = (AdjMatrixNode) f.newNode(0, coordinates);
        n2 = (AdjMatrixNode) f.newNode(1, 2);
        n3 = (AdjMatrixNode) f.newNode("aa",2);
        n2.setCoordinate(0, 1);
        n2.setCoordinate(1, 1);
        n3.setCoordinate(0, 2);
        n3.setCoordinate(1, 0);
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
    public void printGraph() {
        buildSimpleGraph();
        System.out.println(g.graphVizExport());
    }
    
}
