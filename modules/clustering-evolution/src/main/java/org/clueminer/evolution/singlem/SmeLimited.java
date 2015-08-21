package org.clueminer.evolution.singlem;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.multim.MultiMuteEvolution;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 * @param <I>
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = Evolution.class)
public class SmeLimited<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>>
        extends MultiMuteEvolution<I, E, C> implements Runnable, Evolution<I, E, C>, Lookup.Provider {

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
    public boolean isValid(I individual) {
        Clustering<E, C> c = null;
        if (individual != null && individual.getClustering() == null) {
            c = individual.updateCustering();
        }
        if (c != null) {
            return c.size() <= maxK;
        }
        return false;
    }
}
