package org.clueminer.evolution.attr;

import org.clueminer.clustering.api.evolution.Pair;
import org.clueminer.clustering.api.evolution.Individual;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.AbstractEvolution;
import org.clueminer.evolution.AbstractIndividual;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Evolution.class)
public class AttrEvolution extends AbstractEvolution implements Runnable, Evolution, Lookup.Provider {

    private boolean isFinished = true;
    private final Random rand = new Random();

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

    private static String name = "Attributes' evolution";
    private static final Logger logger = Logger.getLogger(AttrEvolution.class.getName());

    public AttrEvolution() {
        initEvolution();
    }

    public AttrEvolution(Dataset<Instance> dataset, int generations) {
        this.dataset = dataset;
        this.generations = generations;
        initEvolution();
    }

    private void initEvolution() {
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        isFinished = false;
        avgFitness = new Pair<>();
        bestFitness = new Pair<>();
        time = new Pair<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void run() {
        time.a = System.currentTimeMillis();
        LinkedList<Individual> children = new LinkedList<>();
        Population pop = new Population(this, populationSize);
        avgFitness.a = pop.getAvgFitness();
        Individual best = pop.getBestIndividual();
        bestFitness.a = best.getFitness();
        ArrayList<Individual> selected = new ArrayList<>(populationSize);
        //System.out.println(pop);
        if (ph != null) {
            ph.start(generations);
            ph.progress("starting evolution...");
        }

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
            for (Individual child : children) {
                child.countFitness();
                child.getFitness();
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

        // System.out.println("evolution took " + (end - start) + " ms");
        fireFinalResult(generations, best, time, bestFitness, avgFitness);
        if (ph != null) {
            ph.finish();
        }
    }

}
