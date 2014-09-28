package org.clueminer.events;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class NodeTest {

    public NodeTest() {
    }

    @Test
    public void testGetValue() {
    }

    /**
     * (root) -> (n1) -> (n2) -> (n3)
     *
     * @return
     */
    private Node<String> simpleGraph() {
        Node<String> root = new Node<>("root");
        Node<String> n1 = new Node<>("one");
        Node<String> n2 = new Node<>("two");
        Node<String> n3 = new Node<>("three");
        root.addOutEdge(n1);
        n1.addOutEdge(n2);
        n2.addOutEdge(n3);
        return root;
    }

    @Test
    public void testAddEdge() {
        Node<String> root = simpleGraph();
        assertEquals(1, root.outEdgesCnt());
    }

    @Test
    public void testRemoveEdge() {
    }

    @Test
    public void testEdgesCnt() {
    }

    @Test
    public void testContains() {
        Node<String> root = simpleGraph();
        assertEquals(true, root.containsSucc(root));
    }

}
