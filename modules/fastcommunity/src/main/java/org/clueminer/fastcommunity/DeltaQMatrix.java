package org.clueminer.fastcommunity;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Hamster
 */
public class DeltaQMatrix {

    Map<IntPair, ReverseElement> matrix = new HashMap<>();
    PriorityQueue<ReverseElement> pQ;

    DeltaQMatrix(PriorityQueue<ReverseElement> pq) {
        this.pQ = pq;
    }

    void build(AdjListGraph graph) {
        int edgesCount = graph.getEdgeCount();
        for (Node node : graph.getNodes()) {
            int i = (int) node.getId();
            int iDegree = graph.getDegree(graph.getNode(i));
            for (Node neighbor : graph.getNeighbors(node)) {
                int j = (int) neighbor.getId();
                int jDegree = graph.getDegree(graph.getNode(j));
                double eij = 0.5 / edgesCount;
                double delta = 2 * (eij - ((iDegree / edgesCount) * (jDegree / edgesCount)));
                if (i < j) {
                    matrix.put(new IntPair(i, j), new ReverseElement(delta, i, j));
                } else {
                    matrix.put(new IntPair(j, i), new ReverseElement(delta, j, i));
                }
            }
        }
    }

    ReverseElement get(Integer i, Integer j) {
        return matrix.get(IntPair.ordered(i, j));
    }

    void add(Integer i, Integer j, Double value) {
        ReverseElement element;
        if (i < j) {
            element = new ReverseElement(value, i, j);
        } else {
            element = new ReverseElement(value, j, i);
        }
        matrix.put(IntPair.ordered(i, j), element);
        pQ.add(element);
    }

    void remove(Integer i, Integer j) {
        ReverseElement deleted = matrix.remove(IntPair.ordered(i, j));
        if (deleted != null) {
            pQ.remove(deleted);
        }
    }

}
