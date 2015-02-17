package org.clueminer.evolution.singlem;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
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
        System.out.println("setting maxK to " + maxK);
    }

    @Override
    public boolean isValid(Individual individual) {
        Clustering<? extends Cluster> c = null;
        if (individual != null && individual.getClustering() == null) {
            c = individual.updateCustering();
        }
        if (c != null) {
            return c.size() <= maxK;
        }
        return false;
    }
}
