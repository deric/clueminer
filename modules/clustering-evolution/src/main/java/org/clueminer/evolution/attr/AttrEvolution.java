/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.evolution.attr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.BaseEvolution;
import org.clueminer.evolution.api.AbstractIndividual;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Pair;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
//@ServiceProvider(service = Evolution.class)
public class AttrEvolution<E extends Instance, C extends Cluster<E>> extends BaseEvolution implements Runnable, Evolution, Lookup.Provider {

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

    private int k = 3;

    private static String name = "Attributes' evolution";
    private static final Logger logger = Logger.getLogger(AttrEvolution.class.getName());

    public AttrEvolution() {
        initEvolution();
    }

    public AttrEvolution(Dataset<? extends Instance> dataset, int generations) {
        this.dataset = dataset;
        this.generations = generations;
        //@TODO fetch number of clusters
        algorithm = new KMeans();
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
        evolutionStarted(this);
        time.a = System.currentTimeMillis();
        LinkedList<Individual> children = new LinkedList<>();
        TournamentPopulation pop = new TournamentPopulation(this, populationSize, WeightsIndividual.class);
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
                Individual current = pop.getIndividual(i).deepCopy();
                current.mutate();
                // put mutated individual to the list of new individuals
                children.add(current);
            }
            double fitness;
            for (Individual child : children) {
                child.countFitness();
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
            Clustering<E, C> clustering = bestInd.getClustering();
            instanceContent.add(clustering);
            fireBestIndividual(g, pop);
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

    @Override
    public Individual createIndividual() {
        return new WeightsIndividual(this);
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

}
