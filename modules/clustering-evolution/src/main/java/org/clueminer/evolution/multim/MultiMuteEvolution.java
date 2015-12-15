package org.clueminer.evolution.multim;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.factory.LinkageFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.distance.api.Distance;
import org.clueminer.evolution.BaseEvolution;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Pair;
import org.clueminer.evolution.api.Population;
import org.clueminer.evolution.attr.TournamentPopulation;
import org.clueminer.math.Matrix;
import org.clueminer.math.StandardisationFactory;
import org.clueminer.std.Scaler;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 * @param <I>
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = Evolution.class)
public class MultiMuteEvolution<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>>
        extends BaseEvolution<I, E, C> implements Runnable, Evolution<I, E, C>, Lookup.Provider {

    private static final String name = "muti-mute";
    protected Executor<E, C> exec;
    protected List<Distance> dist;
    protected List<ClusterLinkage<E>> linkage;
    private static final Logger logger = Logger.getLogger(MultiMuteEvolution.class.getName());
    protected List<String> stds;
    protected final Random rand = new Random();
    protected ObjectOpenHashSet<String> tabu;
    protected boolean isFinished = false;
    protected Population<I> population;

    /**
     * for start and final average fitness
     */
    protected Pair<Double, Double> avgFitness;
    /**
     * for start and final best fitness in whole population
     */
    protected Pair<Double, Double> bestFitness;
    /**
     * for star and final time
     */
    protected Pair<Long, Long> time;

    public MultiMuteEvolution() {
        //cache normalized datasets
        init(new ClusteringExecutorCached<E, C>());
    }

    public MultiMuteEvolution(Executor executor) {
        init(executor);
    }

    protected final void init(Executor<E, C> executor) {
        this.exec = executor;
        algorithm = new HCLW();
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        prepare();
    }

    @Override
    public String getName() {
        return name;
    }

    protected void prepare() {
        StandardisationFactory sf = StandardisationFactory.getInstance();
        stds = sf.getProviders();
        DistanceFactory df = DistanceFactory.getInstance();
        dist = df.getAll();
        LinkageFactory lf = LinkageFactory.getInstance();
        linkage = lf.getAll();
        prepareHook();
    }

    /**
     * Could be overridden by inheriting classes
     */
    protected void prepareHook() {
        //is initialized before starting evolution
    }

    protected void beforeRunHook() {
        //executed when evolution starts
    }

    protected void clean() {
        if (dataset == null) {
            throw new RuntimeException("missing data");
        }
        isFinished = false;
        avgFitness = new Pair<>();
        bestFitness = new Pair<>();
        time = new Pair<>();
        tabu = new ObjectOpenHashSet<>();
    }

    protected void printStarted() {
        if (ph != null) {
            int workunits = getGenerations();
            logger.log(Level.INFO, "stds: {0}", stds.size());
            logger.log(Level.INFO, "distances: {0}", dist.size());
            logger.log(Level.INFO, "linkages: {0}", linkage.size());
            ph.start(workunits);
            ph.progress("starting " + getName() + "evolution...");
        }
    }

    @Override
    public void run() {
        beforeRunHook();
        evolutionStarted(this);
        clean();
        printStarted();

        time.a = System.currentTimeMillis();
        LinkedList<I> children = new LinkedList<>();
        population = new TournamentPopulation(this, populationSize, MultiMuteIndividual.class);
        avgFitness.a = population.getAvgFitness();
        Individual best = population.getBestIndividual();
        bestFitness.a = best.getFitness();
        ArrayList<I> selected = new ArrayList<>(populationSize);
        double fitness;
        for (int g = 0; g < generations && !isFinished; g++) {

            // clear collection for new individuals
            children.clear();

            // apply mutate operator
            for (int i = 0; i < population.size(); i++) {
                I current = population.getIndividual(i).deepCopy();
                current.mutate();
                if (this.isValid(current) && current.isValid()) {
                    if (!isItTabu(current.toString())) {
                        // put mutated individual to the list of new individuals
                        fitness = current.countFitness();
                        if (!Double.isNaN(fitness)) {
                            children.add(current);
                            //update meta-database
                            fireIndividualCreated(current);
                        }
                    }
                }
            }

            logger.log(Level.INFO, "gen: {0}, num children: {1}", new Object[]{g, children.size()});
            selected.clear();
            // merge new and old individuals
            for (int i = children.size(); i < population.size(); i++) {
                I tmpi = population.getIndividual(i).deepCopy();
                tmpi.countFitness();
                selected.add(tmpi);
            }

            for (I ind : children) {
                fitness = ind.getFitness();
                if (!Double.isNaN(fitness)) {
                    selected.add(ind);
                }
            }

            // sort them by fitness (thanks to Individual implements interface Comparable)
            Individual[] newIndsArr = selected.toArray(new Individual[0]);
            //  for (int i = 0; i < newIndsArr.length; i++) {
            //      System.out.println(i + ": " + newIndsArr[i].getFitness());
            //  }
            if (maximizedFitness) {
                Arrays.sort(newIndsArr, Collections.reverseOrder());
            } else {
                //natural ordering
                Arrays.sort(newIndsArr);
            }

            int indsToCopy;
            if (newIndsArr.length > population.size()) {
                indsToCopy = population.size();
            } else {
                indsToCopy = newIndsArr.length;
            }
            if (ph != null) {
                ph.progress(indsToCopy + " new individuals in population. generation: " + g);
            }
            if (indsToCopy > 0) {
                //System.out.println("copying " + indsToCopy);
                //TODO: old population should be sorted as well? take only part of the new population?
                System.arraycopy(newIndsArr, 0, population.getIndividuals(), 0, indsToCopy);
            } else {
                logger.log(Level.WARNING, "no new individuals in generation = {0}", g);
                //    throw new RuntimeException("no new individuals");
            }

            // print statistic
            // System.out.println("gen: " + g + "\t bestFit: " + pop.getBestIndividual().getFitness() + "\t avgFit: " + pop.getAvgFitness());
            Individual bestInd = population.getBestIndividual();
            Clustering<E, C> clustering = bestInd.getClustering();
            instanceContent.add(clustering);
            fireBestIndividual(g, population);
            if (ph != null) {
                ph.progress(g);
            }
        }

        time.b = System.currentTimeMillis();
        population.sortByFitness();
        avgFitness.b = population.getAvgFitness();
        best = population.getBestIndividual();
        bestFitness.b = best.getFitness();
        fireFinalResult(generations, (I) best, time, bestFitness, avgFitness);

        finish();
    }

    public Matrix standartize(Dataset<? extends Instance> data, String method, boolean logScale) {
        return Scaler.standartize(data.arrayCopy(), method, logScale);
    }

    protected void finish() {
        if (ph != null) {
            ph.finish();
        }
    }

    /**
     * We blacklist solutions which we already computed
     *
     * @param config
     * @return
     */
    protected boolean isItTabu(String config) {
        return tabu.contains(config);
    }

    @Override
    public void setAlgorithm(ClusteringAlgorithm algorithm) {
        this.algorithm = algorithm;
        exec.setAlgorithm(algorithm);
    }

    @Override
    public I createIndividual() {
        return (I) new MultiMuteIndividual(this);
    }

}
