package org.clueminer.evolution.singlem;

import java.util.HashSet;
import java.util.logging.Logger;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.api.evolution.Pair;
import org.clueminer.clustering.api.evolution.Population;
import org.clueminer.evolution.multim.MultiMuteEvolution;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Tomas Barton
 */
public class SingleMuteEvolution extends MultiMuteEvolution implements Runnable, Evolution, Lookup.Provider {

    private static final String name = "single-mute";
    private static final Logger logger = Logger.getLogger(SingleMuteEvolution.class.getName());
    private HashSet<String> tabu;
    private boolean isFinished = true;
    private Population<? extends Individual> population;

    /**
     * for start and final average fitness
     */
    private Pair<Double, Double> avgFitness;
    /**
     * for start and final best fitness in whole population
     */
    private Pair<Double, Double> bestFitness;
    /**
     * for star and final time
     */
    private Pair<Long, Long> time;

    public SingleMuteEvolution() {
        //cache normalized datasets
        this.exec = new ClusteringExecutorCached();
        init();
    }

    private void init() {
        algorithm = new HACLW();
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        prepare();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Individual createIndividual() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
