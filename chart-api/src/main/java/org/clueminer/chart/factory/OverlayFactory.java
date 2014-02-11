package org.clueminer.chart.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.chart.api.Overlay;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class OverlayFactory extends ServiceFactory<Overlay> {

    private static OverlayFactory instance;

    public static OverlayFactory getInstance() {
        if (instance == null) {
            instance = new OverlayFactory();
        }
        return instance;
    }

    private OverlayFactory() {
        providers = new LinkedHashMap<String, Overlay>();
        Collection<? extends Overlay> list = Lookup.getDefault().lookupAll(Overlay.class);
        for (Overlay c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

}
