package org.clueminer.evolution;

import org.clueminer.clustering.api.evolution.Pair;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.api.evolution.EvolutionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Evolution.class)
public class AttrEvolution implements Runnable, Evolution, Lookup.Provider {

    private int populationSize = 100;
    private int generations;
    private Dataset<? extends Instance> dataset;
    private boolean isFinished = true;
    private Random rand = new Random();
    /**
     * Probability of mutation
     */
    protected double mutationProbability = 0.3;
    /**
     * Probability of crossover
     */
    protected double crossoverProbability = 0.3;
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
    protected ClusterEvaluation evaluator;
    protected ClusterEvaluation external;
    protected ClusteringAlgorithm algorithm;
    private boolean maximizedFitness;
    private static String name = "Attributes' evolution";
    private static final Logger logger = Logger.getLogger(AttrEvolution.class.getName());
    private transient InstanceContent instanceContent;
    private transient Lookup lookup;

    public AttrEvolution() {
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);

        isFinished = false;
        avgFitness = new Pair<Double, Double>();
        bestFitness = new Pair<Double, Double>();
        time = new Pair<Long, Long>();
    }

    public AttrEvolution(Dataset<Instance> dataset, int generations) {
        this.dataset = dataset;
        isFinished = false;
        this.generations = generations;
        avgFitness = new Pair<Double, Double>();
        bestFitness = new Pair<Double, Double>();
        time = new Pair<Long, Long>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int attributesCount() {
        return dataset.attributeCount();
    }

    @Override
    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    @Override
    public void setDataset(Dataset<? extends Instance> dataset) {
        this.dataset = dataset;
    }

    @Override
    public void run() {
        time.a = System.currentTimeMillis();
        LinkedList<Individual> children = new LinkedList<Individual>();
        Population pop = new Population(this, populationSize);
        avgFitness.a = pop.getAvgFitness();
        Individual best = pop.getBestIndividual();
        bestFitness.a = best.getFitness();
        ArrayList<Individual> selected = new ArrayList<Individual>(populationSize);
        //System.out.println(pop);

        for (int g = 0; g < generations && !isFinished; g++) {

            // clear collection for new individuals
            children.clear();

            // apply crossover operator
            for (int i = 0; i < pop.getIndividuals().length; i++) {
                if (rand.nextDouble() < getCrossoverProbability()) {
                    // take copy of current individual
                    Individual thisOne = pop.getIndividual(i);
                    // take another individual
                    List<? extends Individual> second = pop.selectIndividuals(1);
                    // do crossover
                    List<Individual> ancestors = thisOne.cross(second.get(0));
                    // put childrens to the list of new individuals
                    children.addAll(ancestors);
                }
            }

            // apply mutate operator
            for (int i = 0; i < pop.getIndividuals().length; i++) {
                Individual thisOne = pop.getIndividual(i).deepCopy();
                thisOne.mutate();
                // put mutated individual to the list of new individuals
                children.add(thisOne);
            }
            double fitness;
            // count fitness of all changed individuals
            for (int i = 0; i < children.size(); i++) {
                children.get(i).countFitness();
                children.get(i).getFitness();
            }
            selected.clear();
            // merge new and old individuals
            for (int i = children.size(); i < pop.individualsLength(); i++) {
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
            if (newIndsArr.length > pop.individualsLength()) {
                indsToCopy = pop.individualsLength();
            } else {
                indsToCopy = newIndsArr.length;
            }
            System.out.println("individuals in population: " + indsToCopy);
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
            fireBestIndividual(g, pop.getBestIndividual(), pop.getAvgFitness());
        }

        time.b = System.currentTimeMillis();
        pop.sortByFitness();
        avgFitness.b = pop.getAvgFitness();
        best = pop.getBestIndividual();
        bestFitness.b = best.getFitness();

        // System.out.println("evolution took " + (end - start) + " ms");
        fireFinalResult(generations, best, time, bestFitness, avgFitness);
    }

    private double externalValidation(Individual best) {
        if (external != null) {
            return external.score(best.getClustering(), dataset);
        }
        return Double.NaN;
    }
    private transient EventListenerList evoListeners = new EventListenerList();

    private void fireBestIndividual(int generationNum, Individual best, double avgFitness) {
        EvolutionListener[] listeners;

        if (evoListeners != null) {
            listeners = evoListeners.getListeners(EvolutionListener.class);
            for (EvolutionListener listener : listeners) {
                listener.bestInGeneration(generationNum, best, avgFitness, externalValidation(best));
            }
        }
    }

    @Override
    public void addEvolutionListener(EvolutionListener listener) {
        evoListeners.add(EvolutionListener.class, listener);
    }

    private void fireFinalResult(int g, Individual best, Pair<Long, Long> time,
            Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness) {
        EvolutionListener[] listeners;

        if (evoListeners != null) {
            listeners = evoListeners.getListeners(EvolutionListener.class);
            for (EvolutionListener listener : listeners) {
                listener.finalResult(this, g, best, time, bestFitness, avgFitness, externalValidation(best));
            }
        }
    }

    @Override
    public double getMutationProbability() {
        return mutationProbability;
    }

    @Override
    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    @Override
    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    /**
     *
     * @param crossoverProbability
     */
    @Override
    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    @Override
    public ClusteringAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(ClusteringAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public ClusterEvaluation getEvaluator() {
        return evaluator;
    }

    @Override
    public void setEvaluator(ClusterEvaluation evaluator) {
        this.evaluator = evaluator;
        maximizedFitness = evaluator.compareScore(1.0, 0.0);
    }

    /**
     * External validation criterion, is used only for reporting, not during
     * evolution
     *
     * @return
     */
    @Override
    public ClusterEvaluation getExternal() {
        return external;
    }

    @Override
    public void setExternal(ClusterEvaluation external) {
        this.external = external;
    }

    @Override
    public int getPopulationSize() {
        return populationSize;
    }

    @Override
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    @Override
    public boolean isMaximizedFitness() {
        return maximizedFitness;
    }

    public int getGenerations() {
        return generations;
    }

    @Override
    public void setGenerations(int generations) {
        this.generations = generations;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }
}
