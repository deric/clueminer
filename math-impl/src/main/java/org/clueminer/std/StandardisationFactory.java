package org.clueminer.std;

import org.clueminer.math.Standardisation;
import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class StandardisationFactory extends ServiceFactory<Standardisation> {

    private static StandardisationFactory instance;

    public static StandardisationFactory getDefault() {
        if (instance == null) {
            instance = new StandardisationFactory();
        }
        return instance;
    }

    private StandardisationFactory() {
        providers = new LinkedHashMap<String, Standardisation>();
        Collection<? extends Standardisation> list = Lookup.getDefault().lookupAll(Standardisation.class);
        for (Standardisation c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
