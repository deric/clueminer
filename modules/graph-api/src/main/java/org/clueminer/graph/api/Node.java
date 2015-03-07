package org.clueminer.graph.api;

import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public interface Node extends Element {

    public void setInstance(Instance i);

    public Instance getInstance();

}
