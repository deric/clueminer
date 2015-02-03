package org.clueminer.meta.h2;

import java.util.Collection;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.api.evolution.Pair;
import org.clueminer.clustering.api.evolution.Population;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.meta.api.MetaFeed;

/**
 *
 * @author Tomas Barton
 */
public class H2Listener implements MetaFeed {

    private H2Store store;

    @Override
    public void started(Evolution evolution) {
        store = H2Store.getInstance();
    }

    @Override
    public void resultUpdate(Collection<Clustering<? extends Cluster>> result) {

    }

    @Override
    public void bestInGeneration(int generationNum, Population<? extends Individual> population, double external) {

    }

    @Override
    public void finalResult(Evolution evolution, int g, Individual best, Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external) {

    }

    @Override
    public void individualCreated(Dataset<? extends Instance> dataset, Individual individual) {
        store.add(dataset, individual.getClustering());
    }

}
