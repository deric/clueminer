package org.clueminer.clustering.api.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class CutoffStrategyFactory extends ServiceFactory<CutoffStrategy> {

    private static CutoffStrategyFactory instance;

    public static CutoffStrategyFactory getInstance() {
        if (instance == null) {
            instance = new CutoffStrategyFactory();
        }
        return instance;
    }

    private CutoffStrategyFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends CutoffStrategy> list = Lookup.getDefault().lookupAll(CutoffStrategy.class);
        for (CutoffStrategy c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
