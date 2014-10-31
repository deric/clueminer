package org.clueminer.clustering.api.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.clustering.gui.ClusteringExport;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringExportFactory extends ServiceFactory<ClusteringExport> {

    private static ClusteringExportFactory instance;

    public static ClusteringExportFactory getInstance() {
        if (instance == null) {
            instance = new ClusteringExportFactory();
        }
        return instance;
    }

    private ClusteringExportFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends ClusteringExport> list = Lookup.getDefault().lookupAll(ClusteringExport.class);
        for (ClusteringExport c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
