package org.clueminer.meta.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class MetaStorageFactory extends ServiceFactory<MetaStorage> {

    private static MetaStorageFactory instance;

    public static MetaStorageFactory getInstance() {
        if (instance == null) {
            instance = new MetaStorageFactory();
        }
        return instance;
    }

    private MetaStorageFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends MetaStorage> list = Lookup.getDefault().lookupAll(MetaStorage.class);
        for (MetaStorage c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
