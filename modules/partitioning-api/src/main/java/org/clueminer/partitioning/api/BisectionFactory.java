package org.clueminer.partitioning.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Bruna
 */
public class BisectionFactory extends ServiceFactory<Bisection> {

    private static BisectionFactory instance;

    public static BisectionFactory getInstance() {
        if (instance == null) {
            instance = new BisectionFactory();
        }
        return instance;
    }

    private BisectionFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends Bisection> list = Lookup.getDefault().lookupAll(Bisection.class);
        for (Bisection c : list) {
            providers.put(c.getName(), c);
        }
    }
}
