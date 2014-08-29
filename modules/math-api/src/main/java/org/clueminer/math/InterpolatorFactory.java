package org.clueminer.math;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class InterpolatorFactory extends ServiceFactory<Interpolator> {

    private static InterpolatorFactory instance;

    public static InterpolatorFactory getInstance() {
        if (instance == null) {
            instance = new InterpolatorFactory();
        }
        return instance;
    }

    private InterpolatorFactory() {
        providers = new LinkedHashMap<String, Interpolator>();
        Collection<? extends Interpolator> list = Lookup.getDefault().lookupAll(Interpolator.class);
        for (Interpolator c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
