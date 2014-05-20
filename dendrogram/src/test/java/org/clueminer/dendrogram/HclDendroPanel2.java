package org.clueminer.dendrogram;

import org.clueminer.dendrogram.gui.DendrogramViewer;

/**
 *
 * @author Tomas Barton
 */
public class HclDendroPanel2 extends HclDendroPanel {

    private static final long serialVersionUID = 1839749571569545084L;

    @Override
    public void initViewer() {
        viewer = new DendrogramViewer();
    }

}
