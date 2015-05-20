package org.clueminer.fastcommunity;

import java.util.HashSet;
import java.util.Set;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Hamster
 */
public class Community {

    private final Integer id;
    private final Set<Node> nodes;
    private Integer edgesInside;
    private Integer edgesOutside;

    public Community(Community src) {
        this.id = -1;
        this.nodes = src.nodes;
        this.edgesInside = src.edgesInside;
        this.edgesOutside = src.edgesOutside;
    }

    public Community(Graph graph, int id, Node node) {
        this.id = id;
        this.nodes = new HashSet<>();
        this.edgesInside = 0;
        nodes.add(node);
        this.edgesOutside = graph.getDegree(node);
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Integer getId() {
        return id;
    }

    public Integer getEdgesInside() {
        return edgesInside;
    }

    public Integer getEdgesOutside() {
        return edgesOutside;
    }

    public void setEdgesOutside(Integer edgesOutside) {
        this.edgesOutside = edgesOutside;
    }

    public void addAll(Community other) {
        this.nodes.addAll(other.getNodes());
    }

    void addInsideEdges(int i) {
        edgesInside += i;
    }

    @Override
    public String toString() {
        String res = "c" + id + "\n";
        for (Node node : nodes) {
            res += "\t" + node.getId() + "\n";
        }
        return res;
    }

}
