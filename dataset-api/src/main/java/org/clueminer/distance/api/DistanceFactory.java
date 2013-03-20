package org.clueminer.distance.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class DistanceFactory extends ServiceFactory<AbstractDistance> {

    private static DistanceFactory instance;

    public static DistanceFactory getDefault() {
        if (instance == null) {
            instance = new DistanceFactory();
        }
        return instance;
    }

    private DistanceFactory() {
        providers = new LinkedHashMap<String, AbstractDistance>();
        Collection<? extends AbstractDistance> list = Lookup.getDefault().lookupAll(AbstractDistance.class);
        for (AbstractDistance c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}