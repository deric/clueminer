/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.graph.fast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.Node;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class FastGraphTest<E extends Instance> {

    private static final GraphFactoryImpl factory = GraphFactoryImpl.getInstance();
    private static final FastGraph graph = new FastGraph();
    private static final ArrayList<Node> nodes = new ArrayList<>();
    private static final ArrayList<Edge> edges = new ArrayList<>();
    private Node n1;
    private Node n2;
    private Node n3;
    private Edge e1;
    private Edge e2;
    private Edge e3;
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
    public void testAddNode() {
        FastGraph graphStore = new FastGraph();
        NodeImpl[] nodes = GraphGenerator.generateNodeList(1);

        boolean a = graphStore.addNode(nodes[0]);
        boolean b = graphStore.addNode(nodes[0]);

        Assert.assertTrue(a);
        Assert.assertFalse(b);

        boolean c = graphStore.contains(nodes[0]);

        Assert.assertTrue(c);
    }

    public static Graph buildSmallGraph(Graph g, GraphBuilder factory) {
        g.ensureCapacity(7);
        Node n1 = factory.newNode();
        Node n2 = factory.newNode();
        Node n3 = factory.newNode();
        Node n4 = factory.newNode();
        Node n5 = factory.newNode();
        Node n6 = factory.newNode();
        Node n7 = factory.newNode();

        Edge e1 = factory.newEdge(n1, n2);
        Edge e2 = factory.newEdge(n1, n3);
        Edge e3 = factory.newEdge(n1, n7);
        Edge e4 = factory.newEdge(n2, n6);
        Edge e5 = factory.newEdge(n3, n4);
        Edge e6 = factory.newEdge(n3, n5);
        Edge e7 = factory.newEdge(n4, n5);
        Edge e8 = factory.newEdge(n4, n6);
        Edge e9 = factory.newEdge(n4, n7);
        Edge e10 = factory.newEdge(n5, n7);
        Edge e11 = factory.newEdge(n6, n7);

        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addNode(n4);
        g.addNode(n5);
        g.addNode(n6);
        g.addNode(n7);

        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);
        g.addEdge(e4);
        g.addEdge(e5);
        g.addEdge(e6);
        g.addEdge(e7);
        g.addEdge(e8);
        g.addEdge(e9);
        g.addEdge(e10);
        g.addEdge(e11);

        return g;
    }

    @Test
    public void testAddEdge() {
        FastGraph graphStore = new FastGraph();
        NodeImpl[] nodes = GraphGenerator.generateNodeList(2);
        graphStore.addAllNodes(Arrays.asList(nodes));

        EdgeImpl edge = new EdgeImpl(0l, nodes[0], nodes[1], 1.0, true);
        boolean a = graphStore.addEdge(edge);
        boolean b = graphStore.addEdge(edge);

        Assert.assertTrue(a);
        Assert.assertFalse(b);

        boolean c = graphStore.contains(edge);

        Assert.assertTrue(c);
    }

    //@Test
    public void testRemoveNodeWithEdges() {
        FastGraph graphStore = new FastGraph();
        NodeImpl[] nodes = GraphGenerator.generateSmallNodeList();
        graphStore.addAllNodes(Arrays.asList(nodes));

        EdgeImpl[] edges = GraphGenerator.generateEdgeList(graphStore.nodeStore, 100, 0, true, true);
        graphStore.addAllEdges(Arrays.asList(edges));

        int edgeCount = graphStore.getEdgeCount();
        assertEquals(100, edgeCount);
        Iterator<Node> nodeIterator = graphStore.getNodes().iterator();
        for (; nodeIterator.hasNext();) {
            NodeImpl n = (NodeImpl) nodeIterator.next();
            int degree = n.getDegree();

            boolean hasSelfLoop = graphStore.getEdge(n, n, 0) != null;
            if (hasSelfLoop) {
                degree--;
            }

            nodeIterator.remove();

            assertEquals(graphStore.getEdgeCount(), edgeCount - degree);
            edgeCount -= degree;
        }

        Assert.assertEquals(edgeCount, 0);
        Assert.assertEquals(graphStore.getNodeCount(), 0);
    }

}
