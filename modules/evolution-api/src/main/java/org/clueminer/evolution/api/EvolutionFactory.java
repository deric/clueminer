package org.clueminer.evolution.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class EvolutionFactory extends ServiceFactory<Evolution> {

    private static EvolutionFactory instance;

    public static EvolutionFactory getInstance() {
        if (instance == null) {
            instance = new EvolutionFactory();
        }
        return instance;
    }

    private EvolutionFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends Evolution> list = Lookup.getDefault().lookupAll(Evolution.class);
        for (Evolution c : list) {
            providers.put(c.getName(), c);
        }
    }
}
