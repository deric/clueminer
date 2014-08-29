package org.clueminer.chart.overlay;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Tomas Barton
 */
class OverlayNode extends AbstractNode {

    private OverlayProperties properties;

    public OverlayNode(Children children) {
        super(children);

    }

    public OverlayNode(OverlayProperties properties) {
        super(Children.LEAF);
        this.properties = properties;
    }

}
