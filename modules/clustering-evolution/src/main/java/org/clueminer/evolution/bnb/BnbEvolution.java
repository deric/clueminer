package org.clueminer.evolution.bnb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.LinkageFactory;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.api.evolution.Pair;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.evolution.AbstractEvolution;
import org.clueminer.evolution.AbstractIndividual;
import org.clueminer.evolution.attr.Population;
import org.clueminer.evolution.hac.BaseIndividual;
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
 */
@ServiceProvider(service = Evolution.class)
public class BnbEvolution extends AbstractEvolution implements Runnable, Evolution, Lookup.Provider {

    private static final String name = "BnB";
    protected final Executor exec;
    private int gen;
    protected List<DistanceMeasure> dist;
    protected List<ClusterLinkage> linkage;
    private static final Logger logger = Logger.getLogger(BnbEvolution.class.getName());
    private int cnt;
    protected List<String> standartizations;
    protected final Random rand = new Random();
    private HashSet<String> tabu;
    private boolean isFinished = true;

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

    public BnbEvolution() {
        //cache normalized datasets
        this.exec = new ClusteringExecutorCached();
    }

    public BnbEvolution(Executor executor) {
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        //TODO allow changing algorithm used
        this.exec = executor;
        gen = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    private void prepare() {
        if (dataset == null) {
            throw new RuntimeException("missing data");
        }
        StandardisationFactory sf = StandardisationFactory.getInstance();
        standartizations = sf.getProviders();
        DistanceFactory df = DistanceFactory.getInstance();
        dist = df.getAll();
        LinkageFactory lf = LinkageFactory.getInstance();
        linkage = lf.getAll();
        isFinished = false;
        avgFitness = new Pair<>();
        bestFitness = new Pair<>();
        time = new Pair<>();
    }

    @Override
    public void run() {
        prepare();
        int stdMethods = standartizations.size();

        if (ph != null) {
            int workunits = getGenerations();
            logger.log(Level.INFO, "stds: {0}", stdMethods);
            logger.log(Level.INFO, "distances: {0}", dist.size());
            logger.log(Level.INFO, "linkages: {0}", linkage.size());
            ph.start(workunits);
            ph.progress("starting evolution...");
        }
        cnt = 0;

        time.a = System.currentTimeMillis();
        LinkedList<Individual> children = new LinkedList<>();
        Population pop = new Population(this, populationSize);
        avgFitness.a = pop.getAvgFitness();
        Individual best = pop.getBestIndividual();
        bestFitness.a = best.getFitness();
        ArrayList<Individual> selected = new ArrayList<>(populationSize);

        for (int g = 0; g < generations && !isFinished; g++) {

            // clear collection for new individuals
            children.clear();

            // apply mutate operator
            for (int i = 0; i < pop.size(); i++) {
                Individual thisOne = pop.getIndividual(i).deepCopy();
                thisOne.mutate();
                if (!isItTabu(thisOne.toString())) {
                    // put mutated individual to the list of new individuals
                    children.add(thisOne);
                }
            }
            double fitness;
            logger.log(Level.INFO, "gen: {0}, num children: {1}", new Object[]{g, children.size()});
            for (Individual child : children) {
                child.countFitness();
                child.getFitness();
            }
            selected.clear();
            // merge new and old individuals
            for (int i = children.size(); i < pop.size(); i++) {
                Individual tmpi = pop.getIndividual(i).deepCopy();
                tmpi.countFitness();
                selected.add(tmpi);
            }

            for (Individual ind : children) {
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
            if (newIndsArr.length > pop.size()) {
                indsToCopy = pop.size();
            } else {
                indsToCopy = newIndsArr.length;
            }
            if (ph != null) {
                ph.progress(indsToCopy + " new individuals in population. generation: " + g);
            }
            if (indsToCopy > 0) {
                //System.out.println("copying " + indsToCopy);
                //TODO: old population should be sorted as well? take only part of the new population?
                System.arraycopy(newIndsArr, 0, pop.getIndividuals(), 0, indsToCopy);
            } else {
                logger.log(Level.WARNING, "no new individuals in generation = {0}", g);
                //    throw new RuntimeException("no new individuals");
            }

            // print statistic
            // System.out.println("gen: " + g + "\t bestFit: " + pop.getBestIndividual().getFitness() + "\t avgFit: " + pop.getAvgFitness());
            AbstractIndividual bestInd = pop.getBestIndividual();
            Clustering<Cluster> clustering = bestInd.getClustering();
            instanceContent.add(clustering);
            fireBestIndividual(g, bestInd, pop.getAvgFitness());
            if (ph != null) {
                ph.progress(g);
            }
        }

        time.b = System.currentTimeMillis();
        pop.sortByFitness();
        avgFitness.b = pop.getAvgFitness();
        best = pop.getBestIndividual();
        bestFitness.b = best.getFitness();

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
    private boolean isItTabu(String config) {
        return tabu.contains(config);
    }

    protected void individualCreated(Clustering<? extends Cluster> clustering) {
        instanceContent.add(clustering);
        fireBestIndividual(gen++, new BaseIndividual(clustering), getEvaluator().score((Clustering<Cluster>) clustering, dataset));
    }

    @Override
    public void setAlgorithm(ClusteringAlgorithm algorithm) {
        throw new UnsupportedOperationException("not supported yet");
    }
}
