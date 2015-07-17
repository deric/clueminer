package org.clueminer.graph.adjacencyList;

import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;

/**
 *
 * @author Hamster
 */
public class AdjListNode implements Node {

    private final long id;
    private Object label;
    private Instance instance;

    public AdjListNode(long id) {
        this.id = id;
    }

    public AdjListNode(long id, Object label) {
        this.label = label;
        this.id = id;
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
        return builder.toString();
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
