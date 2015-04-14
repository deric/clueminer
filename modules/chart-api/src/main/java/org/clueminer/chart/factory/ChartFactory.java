package org.clueminer.chart.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.chart.api.Chart;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class ChartFactory extends ServiceFactory<Chart> {

    private static ChartFactory instance;

    public static ChartFactory getInstance() {
        if (instance == null) {
            instance = new ChartFactory();
        }
        return instance;
    }

    private ChartFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends Chart> list = Lookup.getDefault().lookupAll(Chart.class);
        for (Chart c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

}
