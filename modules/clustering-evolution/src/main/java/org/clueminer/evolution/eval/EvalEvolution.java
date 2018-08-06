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
package org.clueminer.evolution.eval;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.exec.ClusteringExecutorCached;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Executor;
import org.clueminer.dataset.api.Instance;
import org.clueminer.events.ListenerList;
import org.clueminer.evolution.api.EvolutionMO;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.attr.TournamentPopulation;
import org.clueminer.evolution.mo.SolTransformer;
import org.clueminer.evolution.multim.MultiMuteEvolution;
import org.clueminer.evolution.multim.MultiMuteIndividual;
import org.clueminer.oo.api.OpListener;
import org.clueminer.oo.api.OpSolution;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author deric
 */
public class EvalEvolution<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>>
        extends MultiMuteEvolution<I, E, C> implements Runnable, EvolutionMO<I, E, C>, Lookup.Provider {

    private static final String name = "MOE";
    private static final Logger LOG = LoggerFactory.getLogger(EvalEvolution.class);
    protected List<ClusterEvaluation<E, C>> objectives;
    private int numSolutions = 5;
    private boolean kLimit;
    protected final transient ListenerList<OpListener> moListeners = new ListenerList<>();

    public EvalEvolution() {
        init(new ClusteringExecutorCached<E, C>());
    }

    public EvalEvolution(Executor executor) {
        init(executor);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void prepareHook() {
        this.objectives = Lists.newLinkedList();
    }

    @Override
    public void addObjective(ClusterEvaluation eval) {
        objectives.add(eval);
    }

    @Override
    public void removeObjective(ClusterEvaluation eval) {
        objectives.remove(eval);
    }

    @Override
    public ClusterEvaluation getObjective(int idx) {
        return objectives.get(idx);
    }

    @Override
    public List<ClusterEvaluation<E, C>> getObjectives() {
        return objectives;
    }

    @Override
    public int getNumObjectives() {
        return objectives.size();
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

            LOG.info("gen: {}, num children: {}", g, children.size());
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
                LOG.warn("no new individuals in generation = {}", g);
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

    @Override
    public void clearObjectives() {
        if (objectives != null && !objectives.isEmpty()) {
            objectives.clear();
        }
    }

    public void addMOEvolutionListener(OpListener listener) {
        moListeners.add(listener);
    }

    protected void fireEvolutionStarted(EvolutionMO evo) {
        if (moListeners != null) {
            for (OpListener listener : moListeners) {
                listener.started(evo);
            }
        }
    }

    /**
     * Fired when repetitive run of same datasets was finished
     */
    public void fireFinishedBatch() {
        if (moListeners != null) {
            for (OpListener listener : moListeners) {
                listener.finishedBatch();
            }
        }
    }

    protected void fireFinalResult(List<Solution> res) {
        SolTransformer trans = SolTransformer.getInstance();
        List<OpSolution> solutions = trans.transform(res, new LinkedList<>());
        if (solutions != null && solutions.size() > 0) {
            if (moListeners != null) {
                for (OpListener listener : moListeners) {
                    listener.finalResult(solutions);
                }
            }
        } else {
            throw new RuntimeException("transforming solutions failed");
        }
    }

    @Override
    public int getNumSolutions() {
        return numSolutions;
    }

    /**
     * Number of solutions to be returned from evolution
     *
     * @param numSolutions should be lower than population size
     */
    @Override
    public void setNumSolutions(int numSolutions) {
        this.numSolutions = numSolutions;
    }

    public boolean iskLimited() {
        return kLimit;
    }

    public void setkLimit(boolean kLimit) {
        this.kLimit = kLimit;
    }

}
