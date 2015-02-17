package org.clueminer.evolution.singlem;

import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.multim.MultiMuteEvolution;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Evolution.class)
public class SmeLimited extends MultiMuteEvolution implements Runnable, Evolution, Lookup.Provider {

    private static final String name = "SME Limited (k < sqrt(n))";
    private int maxK;

    public SmeLimited() {
        super();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void beforeRunHook() {
        maxK = (int) Math.sqrt(getDataset().size());
    }

    @Override
    public boolean isValid(Individual individual) {
        return individual.getClustering().size() <= maxK;
    }
}
