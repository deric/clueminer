package org.clueminer.clustering.gui;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringDialogFactory extends ServiceFactory<ClusteringDialog> {

    private static ClusteringDialogFactory instance;

    public static ClusteringDialogFactory getInstance() {
        if (instance == null) {
            instance = new ClusteringDialogFactory();
        }
        return instance;
    }

    private ClusteringDialogFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends ClusteringDialog> list = Lookup.getDefault().lookupAll(ClusteringDialog.class);
        for (ClusteringDialog c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

}
