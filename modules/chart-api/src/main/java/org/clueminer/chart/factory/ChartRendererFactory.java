package org.clueminer.chart.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.chart.api.ChartRenderer;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class ChartRendererFactory extends ServiceFactory<ChartRenderer> {

    private static ChartRendererFactory instance;

    public static ChartRendererFactory getInstance() {
        if (instance == null) {
            instance = new ChartRendererFactory();
        }
        return instance;
    }

    private ChartRendererFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends ChartRenderer> list = Lookup.getDefault().lookupAll(ChartRenderer.class);
        for (ChartRenderer c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

}
