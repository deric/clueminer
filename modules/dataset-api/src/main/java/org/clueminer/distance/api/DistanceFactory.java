package org.clueminer.distance.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class DistanceFactory extends ServiceFactory<Distance> {

    private static DistanceFactory instance;

    public static DistanceFactory getInstance() {
        if (instance == null) {
            instance = new DistanceFactory();
        }
        return instance;
    }

    private DistanceFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends Distance> list = Lookup.getDefault().lookupAll(Distance.class);
        for (Distance c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}