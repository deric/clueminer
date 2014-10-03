package org.clueminer.dataset.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class DataStandardizationFactory extends ServiceFactory<DataStandardization> {

    private static DataStandardizationFactory instance;

    public static DataStandardizationFactory getInstance() {
        if (instance == null) {
            instance = new DataStandardizationFactory();
        }
        return instance;
    }

    private DataStandardizationFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends DataStandardization> list = Lookup.getDefault().lookupAll(DataStandardization.class);
        for (DataStandardization c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
