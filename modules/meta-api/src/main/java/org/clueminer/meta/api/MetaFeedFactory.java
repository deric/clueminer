package org.clueminer.meta.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class MetaFeedFactory extends ServiceFactory<MetaFeed> {

    private static MetaFeedFactory instance;

    public static MetaFeedFactory getInstance() {
        if (instance == null) {
            instance = new MetaFeedFactory();
        }
        return instance;
    }

    private MetaFeedFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends MetaFeed> list = Lookup.getDefault().lookupAll(MetaFeed.class);
        for (MetaFeed mf : list) {
            providers.put(mf.getName(), mf);
        }
        sort();
    }
}
