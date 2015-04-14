package org.clueminer.evolution.gui;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class EvolutionExportFactory extends ServiceFactory<EvolutionExport> {

    private static EvolutionExportFactory instance;

    public static EvolutionExportFactory getInstance() {
        if (instance == null) {
            instance = new EvolutionExportFactory();
        }
        return instance;
    }

    private EvolutionExportFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends EvolutionExport> list = Lookup.getDefault().lookupAll(EvolutionExport.class);
        for (EvolutionExport c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
