package org.clueminer.graph.impl;

import java.util.Arrays;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import static org.junit.Assert.assertEquals;
import org.clueminer.graph.api.GraphBuilder;

/**
 *
 * @author Tomas Bruna
 */
public class Commons {

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

    public void metisExportTest(Graph g, String result) {
        String metis = g.metisExport(false);
        System.out.println(metis);
        String[] lines = metis.split("\n");
        String sortedResult = lines[0] + "\n";
        for (int i = 1; i <= g.getNodeCount(); i++) {
            String[] numbers = lines[i].split(" ");
            int[] neighbors = new int[numbers.length];
            for (int j = 0; j < numbers.length; j++) {
                neighbors[j] = Integer.parseInt(numbers[j]);
            }
            Arrays.sort(neighbors);
            for (int j = 0; j < neighbors.length; j++) {
                sortedResult += String.valueOf(neighbors[j]) + " ";
            }
            sortedResult += "\n";
        }
        assertEquals(result, sortedResult);
    }

}
