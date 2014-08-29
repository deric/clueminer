package org.clueminer.clustering.api.dendrogram;

import java.util.EventObject;

/**
 *
 * @author Tomas Barton
 */
public class DendrogramDataEvent extends EventObject {

    private static final long serialVersionUID = -2877961648867364690L;

    public DendrogramDataEvent(Object source) {
        super(source);
    }
}
