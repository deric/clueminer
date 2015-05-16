package org.clueminer.graph.adjacencyList;

import org.clueminer.graph.api.Edge;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class AdjListFactoryTest {

    @Test
    public void singletonTest() {
        AdjListFactory first = AdjListFactory.getInstance();
        AdjListFactory second = AdjListFactory.getInstance();
        Assert.assertSame(first, second);
    }

    /**
     * The order of tests is not guaranteed, thus we have to fetch current node
     * count first
     */
    @Test
    public void newNodeTest() {
        long id;
        long expected = AdjListFactory.getNodeCount();
        id = AdjListFactory.getInstance().newNode().getId();
        assertEquals(expected++, id);
        id = AdjListFactory.getInstance().newNode().getId();
        assertEquals(expected++, id);
        id = AdjListFactory.getInstance().newNode().getId();
        assertEquals(expected++, id);
        id = AdjListFactory.getInstance().newNode().getId();
        assertEquals(expected++, id);
        id = AdjListFactory.getInstance().newNode().getId();
        assertEquals(expected++, id);
    }

    @Test
    public void newEdgeTest() {
        AdjListNode source = new AdjListNode(1);
        AdjListNode target = new AdjListNode(2);
        long expected = AdjListFactory.getEdgeCount();
        Edge edge = AdjListFactory.getInstance().newEdge(source, target);
        Assert.assertEquals(expected++, edge.getId());
        Assert.assertSame(source, edge.getSource());
        Assert.assertSame(target, edge.getTarget());
        source = new AdjListNode(3);
        target = new AdjListNode(4);
        edge = AdjListFactory.getInstance().newEdge(source, target, true);
        Assert.assertEquals(expected++, edge.getId());
        Assert.assertSame(source, edge.getSource());
        Assert.assertSame(target, edge.getTarget());
        Assert.assertTrue(edge.isDirected());
    }
}
