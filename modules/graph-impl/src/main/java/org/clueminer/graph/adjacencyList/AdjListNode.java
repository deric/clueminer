package org.clueminer.graph.adjacencyList;

import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.impl.ElemImpl;

/**
 *
 * @author Hamster
 * @param <E>
 */
public class AdjListNode<E extends Instance> extends ElemImpl implements Node<E> {

    private E instance;

    public AdjListNode(long id) {
        super(id);
    }

    public AdjListNode(long id, Object label) {
        super(id);
        this.label = label;
    }

    @Override
    public void setInstance(E instance) {
        this.instance = instance;
    }

    @Override
    public E getInstance() {
        return instance;
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

}
