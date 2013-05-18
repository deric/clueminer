package org.clueminer.evolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.event.EventListenerList;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evaluation.external.JaccardIndex;

/**
 *
 * @author Tomas Barton
 */
public class Evolution implements Runnable {
    
    private int populationSize = 100;
    private int generations;
    private Dataset<Instance> dataset;
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
    protected ClusteringAlgorithm algorithm;    
    protected ClusterEvaluation jaccard = new JaccardIndex();
    
    public Evolution(Dataset<Instance> dataset, int generations) {
        this.dataset = dataset;
        isFinished = false;
        this.generations = generations;
        avgFitness = new Pair<Double, Double>();
        bestFitness = new Pair<Double, Double>();
        time = new Pair<Long, Long>();
    }
    
    protected int attributesCount() {
        return dataset.attributeCount();
    }
    
    protected Dataset<Instance> getDataset() {
        return dataset;
    }
    
    @Override
    public void run() {
        long start = System.currentTimeMillis();
        long end;
        ArrayList<Individual> newInds = new ArrayList<Individual>();
        Population pop = new Population(this, populationSize);
        avgFitness.a = pop.getAvgFitness();
        Individual best = pop.getBestIndividual();
        bestFitness.a = best.getFitness();
        
        System.out.println(pop);
        
        for (int g = 0; g < generations && !isFinished; g++) {

            // clear collection for new individuals
            newInds.clear();

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
                    newInds.addAll(ancestors);
                }
            }

            // apply mutate operator
            for (int i = 0; i < pop.getIndividuals().length; i++) {
                Individual thisOne = pop.getIndividual(i).deepCopy();
                thisOne.mutate();
                // put mutated individual to the list of new individuals
                newInds.add(thisOne);
            }

            // count fitness of all changed individuals
            for (int i = 0; i < newInds.size(); i++) {
                newInds.get(i).countFitness();
            }

            // merge new and old individuals
            for (int i = newInds.size(); i < pop.individualsLength(); i++) {
                Individual tmpi = pop.getIndividual(i).deepCopy();
                tmpi.countFitness();
                newInds.add(tmpi);
            }

            // sort them by fitness (thanks to Individual implements interface Comparable)
            Individual[] newIndsArr = newInds.toArray(new Individual[0]);
            Arrays.sort(newIndsArr);

            // and take the better "half" (populationSize)
            System.arraycopy(newIndsArr, 0, pop.getIndividuals(), 0, pop.getIndividuals().length);

            // print statistic
            // System.out.println("gen: " + g + "\t bestFit: " + pop.getBestIndividual().getFitness() + "\t avgFit: " + pop.getAvgFitness());
            fireBestIndividual(g, pop.getBestIndividual(), pop.getAvgFitness());                       
        }               
        end = System.currentTimeMillis();
       // System.out.println("evolution took " + (end - start) + " ms");
        fireFinalResult(best, (end - start));
    }
    private transient EventListenerList evoListeners = new EventListenerList();
    
    private void fireBestIndividual(int generationNum, Individual best, double avgFitness) {
        EvolutionListener[] listeners;
        
        if (evoListeners != null) {
            listeners = evoListeners.getListeners(EvolutionListener.class);
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].bestInGeneration(generationNum, best, avgFitness);
            }
        }
    }
    
    public void addEvolutionListener(EvolutionListener listener) {
        evoListeners.add(EvolutionListener.class, listener);
    }
    
    private void fireFinalResult(Individual best, long evolutionTime) {
        EvolutionListener[] listeners;
        
        if (evoListeners != null) {
            listeners = evoListeners.getListeners(EvolutionListener.class);
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].finalResult(best, evolutionTime);
            }
        }
    }
        
    public double getMutationProbability() {
        return mutationProbability;
    }
    
    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }
    
    public double getCrossoverProbability() {
        return crossoverProbability;
    }
    
    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }
    
    public ClusteringAlgorithm getAlgorithm() {
        return algorithm;
    }
    
    public void setAlgorithm(ClusteringAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
    
    public ClusterEvaluation getEvaluator() {
        return evaluator;
    }
    
    public void setEvaluator(ClusterEvaluation evaluator) {
        this.evaluator = evaluator;
    }   
}
