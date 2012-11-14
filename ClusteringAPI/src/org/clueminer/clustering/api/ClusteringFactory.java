package org.clueminer.clustering.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringFactory extends ServiceFactory<ClusteringAlgorithm> {

    private static ClusteringFactory instance;

    public static ClusteringFactory getDefault() {
        if (instance == null) {
            instance = new ClusteringFactory();
        }
        return instance;
    }

    private ClusteringFactory() {
        providers = new LinkedHashMap<String, ClusteringAlgorithm>();
        Collection<? extends ClusteringAlgorithm> list = Lookup.getDefault().lookupAll(ClusteringAlgorithm.class);
        for (ClusteringAlgorithm c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
