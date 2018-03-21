/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.evolution.singlem;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Executor;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.attr.TournamentPopulation;
import org.clueminer.evolution.multim.MultiMuteEvolution;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 * @param <I>
 * @param <C>
 * @param <E>
 */
@ServiceProvider(service = Evolution.class)
public class SingleMuteEvolution<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>>
        extends MultiMuteEvolution<I, E, C> implements Runnable, Evolution<I, E, C>, Lookup.Provider {

    private static final String NAME = "single-mute";
    private static final Logger LOG = LoggerFactory.getLogger(SingleMuteEvolution.class);

    public SingleMuteEvolution() {
        //cache normalized datasets
        init(new ClusteringExecutorCached());
    }

    public SingleMuteEvolution(Executor executor) {
        init(executor);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public I createIndividual() {
        return (I) new SingleMuteIndividual(this);
    }

    @Override
    public void run() {
        LOG.info("starting evolution {}", this.getClass().getName());
        evolutionStarted(this);
        clean();

        printStarted();

        time.a = System.currentTimeMillis();
        LinkedList<I> children = new LinkedList<>();
        population = new TournamentPopulation(this, populationSize, SingleMuteIndividual.class);
        avgFitness.a = population.getAvgFitness();
        Individual best = population.getBestIndividual();
        bestFitness.a = best.getFitness();
        //ArrayList<Individual> selected = new ArrayList<>(populationSize);

        for (int g = 0; g < generations && !isFinished; g++) {
            // clear collection for new individuals
            children.clear();
            double fitness;
            // apply mutate operator
            for (int i = 0; i < population.size(); i++) {
                I current = (I) population.getIndividual(i).deepCopy();

                do {
                    do {
                        current.mutate();
                    } while (isItTabu(current.toString()));
                    fitness = current.countFitness();
                    System.out.println("curr| " + current.getClustering().size() + ": " + fitness);
                    System.out.println(Arrays.toString(current.getClustering().clusterSizes()));
                    if (!Double.isNaN(fitness)) {
                        // put mutated individual to the list of new individuals
                        children.add(current);
                        tabu.add(current.toString());
                        //update meta-database
                        fireIndividualCreated(current);
                    }
                } while (!current.isValid() && !this.isValid(current));
            }
            LOG.info("gen: {}, num children: {}", g, children.size());

            // sort them by fitness (thanks to Individual implements interface Comparable)
            Individual[] nextGen = children.toArray(new Individual[0]);
            if (maximizedFitness) {
                //natural ordering
                Arrays.sort(nextGen);
            } else {
                Arrays.sort(nextGen, Collections.reverseOrder());
            }

            int indsToCopy;
            if (nextGen.length > population.size()) {
                indsToCopy = population.size() / 2;
            } else {
                indsToCopy = nextGen.length / 2;
            }
            if (ph != null) {
                ph.progress(indsToCopy + " new individuals in population. generation: " + g);
            }
            if (indsToCopy > 0) {
                System.out.println("copying " + indsToCopy + " new inds: " + nextGen.length);
                //TODO: old population should be sorted as well? take only part of the new population?
                //replace worser part of population by new ones
                System.out.println("arraycopy: " + (populationSize - indsToCopy - 1) + ", p: " + (populationSize - 1));
                System.arraycopy(nextGen, 0, population.getIndividuals(), populationSize - indsToCopy - 1, indsToCopy);
            } else {
                LOG.warn("no new individuals in generation = {}", g);
                //    throw new RuntimeException("no new individuals");
            }
            //sort whole population
            if (maximizedFitness) {
                //natural ordering
                Arrays.sort(population.getIndividuals());
            } else {
                Arrays.sort(population.getIndividuals(), Collections.reverseOrder());
            }

            // print statistic
            // System.out.println("gen: " + g + "\t bestFit: " + pop.getBestIndividual().getFitness() + "\t avgFit: " + pop.getAvgFitness());
            I bestInd = (I) population.getBestIndividual();
            Clustering<E, C> clustering = bestInd.getClustering();
            instanceContent.add(clustering);
            fireBestIndividual(g, population);
            fireResultUpdate(population.getIndividuals());
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

}
