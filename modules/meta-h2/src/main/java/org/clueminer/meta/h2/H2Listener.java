package org.clueminer.meta.h2;

import java.util.Collection;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.api.evolution.Pair;
import org.clueminer.clustering.api.evolution.Population;
import org.clueminer.meta.api.MetaFeed;

/**
 *
 * @author Tomas Barton
 */
public class H2Listener implements MetaFeed {

    @Override
    public void started(Evolution evolution) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resultUpdate(Collection<Clustering<? extends Cluster>> result) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bestInGeneration(int generationNum, Population<? extends Individual> population, double external) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void finalResult(Evolution evolution, int g, Individual best, Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
