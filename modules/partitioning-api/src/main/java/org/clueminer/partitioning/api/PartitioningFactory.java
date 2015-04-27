package org.clueminer.partitioning.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Bruna
 */
public class PartitioningFactory extends ServiceFactory<Partitioning> {

    private static PartitioningFactory instance;

    public static PartitioningFactory getInstance() {
        if (instance == null) {
            instance = new PartitioningFactory();
        }
        return instance;
    }

    private PartitioningFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends Partitioning> list = Lookup.getDefault().lookupAll(Partitioning.class);
        for (Partitioning c : list) {
            providers.put(c.getName(), c);
        }
    }
}
