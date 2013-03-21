package org.clueminer.clustering.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class LinkageFactory extends ServiceFactory<ClusterLinkage> {

    private static LinkageFactory instance;

    public static LinkageFactory getDefault() {
        if (instance == null) {
            instance = new LinkageFactory();
        }
        return instance;
    }

    private LinkageFactory() {
        providers = new LinkedHashMap<String, ClusterLinkage>();
        Collection<? extends ClusterLinkage> list = Lookup.getDefault().lookupAll(ClusterLinkage.class);
        for (ClusterLinkage c : list) {
            providers.put(c.getName(), c);
        }
    }
}
