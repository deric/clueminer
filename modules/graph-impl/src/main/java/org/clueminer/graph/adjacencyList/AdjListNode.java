package org.clueminer.graph.adjacencyList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.EdgeIterable;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.api.NodeIterable;

/**
 *
 * @author Hamster
 */
public class AdjListNode implements Node {

    private final long id;
    private Object label;
    private final HashMap<Long, Edge> edges;
    private Instance instance;

    public AdjListNode(long id) {
        this.id = id;
        edges = new HashMap<>();
    }

    public AdjListNode(long id, Object label) {
        this.label = label;
        this.id = id;
        edges = new HashMap<>();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Object getLabel() {
        return label;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("n");
        builder.append(id);
        builder.append(": [");
        builder.append(this.getInstance() != null ? this.getInstance().classValue() : "?");
        builder.append("]\n");
        for (Map.Entry<Long, Edge> entrySet : edges.entrySet()) {
            Edge edge = entrySet.getValue();
            builder.append("\te").append(edge.getId()).append(" --> ");
            if (edge.getSource() == this) {
                builder.append("n").append(edge.getTarget().getId());
            } else {
                builder.append("n").append(edge.getSource().getId());
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public void addEdge(Edge edge) {
        edges.put(edge.getId(), (AdjListEdge) edge);
    }

    public Edge getEdge(Node neighbor) {
        System.out.println("gettting: " + neighbor.toString());
        for (Map.Entry<Long, Edge> entrySet : edges.entrySet()) {
            Edge edge = entrySet.getValue();
            if (edge.getSource() == neighbor || edge.getTarget() == neighbor) {
                return edge;
            }
        }
        return null;
    }

    public NodeIterable getNeighbors() {
        List<Node> neighbors = new LinkedList();
        for (Map.Entry<Long, Edge> entrySet : edges.entrySet()) {
            Edge edge = entrySet.getValue();
            if (edge.getSource() != this) {
                neighbors.add(edge.getSource());
            }
            if (edge.getTarget() != this) {
                neighbors.add(edge.getTarget());
            }
        }
        return new AdjListNodeIterable(neighbors);
    }

    public EdgeIterable getEdges() {
        return new AdjListEdgeIterable(edges);
    }

    public int getDegree() {
        return edges.size();
    }

    public boolean isAdjacent(Node node) {
        for (Map.Entry<Long, Edge> entrySet : edges.entrySet()) {
            Edge edge = entrySet.getValue();
            if (edge.getSource() == node || edge.getTarget() == node) {
                return true;
            }
        }
        return false;
    }

    public void removeEdge(Edge edge) {
        edges.remove(edge.getId());
    }

    @Override
    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    @Override
    public Instance getInstance() {
        return instance;
    }

}
