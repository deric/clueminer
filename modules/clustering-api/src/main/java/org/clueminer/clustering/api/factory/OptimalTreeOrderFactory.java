package org.clueminer.clustering.api.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.clustering.api.dendrogram.OptimalTreeOrder;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class OptimalTreeOrderFactory extends ServiceFactory<OptimalTreeOrder> {

    private static OptimalTreeOrderFactory instance;

    public static OptimalTreeOrderFactory getInstance() {
        if (instance == null) {
            instance = new OptimalTreeOrderFactory();
        }
        return instance;
    }

    private OptimalTreeOrderFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends OptimalTreeOrder> list = Lookup.getDefault().lookupAll(OptimalTreeOrder.class);
        for (OptimalTreeOrder c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
