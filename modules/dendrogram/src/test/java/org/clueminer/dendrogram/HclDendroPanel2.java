package org.clueminer.dendrogram;

import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dendrogram.gui.DendrogramViewer;

/**
 *
 * @author Tomas Barton
 */
public class HclDendroPanel2 extends HclDendroPanel {

    private static final long serialVersionUID = 1839749571569545084L;

    public HclDendroPanel2(Map<String, Dataset<? extends Instance>> data) {
        super(new DataProviderMap(data));
    }

    public HclDendroPanel2(DataProviderMap provider) {
        super(provider);
    }

    @Override
    public void initViewer() {
        viewer = new DendrogramViewer();
    }

}
