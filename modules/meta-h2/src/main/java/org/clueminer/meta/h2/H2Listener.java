package org.clueminer.meta.h2;

import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.UpdateFeed;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = UpdateFeed.class)
public class H2Listener implements UpdateFeed {

    private H2Store store;
    public static final String name = "H2 store";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int started(Evolution evolution) {
        store = H2Store.getInstance();
        return store.registerRun(evolution);
    }

    @Override
    public void individualCreated(int runId, Individual individual) {
        store.add(runId, individual.getClustering());
    }

}
