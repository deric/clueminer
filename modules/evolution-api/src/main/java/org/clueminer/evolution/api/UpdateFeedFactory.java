package org.clueminer.evolution.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class UpdateFeedFactory extends ServiceFactory<UpdateFeed> {

    private static UpdateFeedFactory instance;

    public static UpdateFeedFactory getInstance() {
        if (instance == null) {
            instance = new UpdateFeedFactory();
        }
        return instance;
    }

    private UpdateFeedFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends UpdateFeed> list = Lookup.getDefault().lookupAll(UpdateFeed.class);
        for (UpdateFeed mf : list) {
            providers.put(mf.getName(), mf);
        }
        sort();
    }
}
