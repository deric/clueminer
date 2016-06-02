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
package org.clueminer.graph.adjacencyMatrix;

import java.util.Collection;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class AdjMatrixGraphTest extends org.clueminer.graph.impl.Commons {

    AdjMatrixFactory f;
    Node n1;
    Node n2;
    Node n3;
    Edge e1;
    Edge e2;
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
        n1 = f.newNode(coordinates);
        n2 = f.newNode(2);
        n3 = f.newNode(2);
        e1 = f.newEdge(n1, n2, 1, 2, false);
        e2 = f.newEdge(n3, n2, 1, 3, false);
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
        n1 = f.newNode(coordinates);
        n2 = f.newNode(2);
        n3 = f.newNode(2);
        Node n4 = f.newNode();
        e1 = f.newEdge(n1, n2, 1, 2, false);
        e2 = f.newEdge(n3, n2, 1, 3, false);
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

    @Test
    public void metisExportTest() {
        Graph gr = new AdjMatrixGraph();
        gr = buildSmallGraph(gr, new AdjMatrixFactory());
        super.metisExportTest(gr, "7 11\n2 3 7 \n" + "1 6 \n" + "1 4 5 \n" + "3 5 6 7 \n" + "3 4 7 \n" + "2 4 7 \n" + "1 4 5 6 \n");
    }

}
