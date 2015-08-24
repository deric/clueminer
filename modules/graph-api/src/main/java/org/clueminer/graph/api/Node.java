package org.clueminer.graph.api;

import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface Node<E extends Instance> extends Element {

    public void setInstance(E i);

    public E getInstance();

}
