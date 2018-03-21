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
package org.clueminer.evolution.api;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Lookup;

/**
 * Common interface for all evolutionary-based algorithms
 *
 * @author Tomas Barton
 * @param <I>
 * @param <E>
 * @param <C>
 */
public interface Evolution<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>>
        extends Runnable, Lookup.Provider {

    /**
     *
     * @return unique identification of algorithm
     */
    String getName();

    Dataset<E> getDataset();

    /**
     * Set data used during evolutionary process.
     *
     * @param dataset
     */
    void setDataset(Dataset<E> dataset);

    /**
     * Set key-value configuration
     *
     * @param params
     */
    void setConfig(Props params);

    /**
     * Configuration for the whole process
     *
     * @return key-value store that can cast values to requested types
     */
    Props getConfig();

    /**
     * Number of attributes in current dataset
     *
     * @return number greater than 0
     */
    int attributesCount();

    /**
     * Set number of generations in an evolutionary algorithm
     *
     * @param generations
     */
    void setGenerations(int generations);

    /**
     * Number of generations in an evolutionary algorithm
     *
     * @return
     */
    int getGenerations();

    double getMutationProbability();

    void setMutationProbability(double mutationProbability);

    double getCrossoverProbability();

    void setCrossoverProbability(double crossoverProbability);

    ClusteringAlgorithm<E, C> getAlgorithm();

    void setAlgorithm(ClusteringAlgorithm<E, C> algorithm);

    ClusterEvaluation<E, C> getExternal();

    /**
     * Set external objective function
     *
     * @param external
     */
    void setExternal(ClusterEvaluation<E, C> external);

    int getPopulationSize();

    void setPopulationSize(int populationSize);

    void addEvolutionListener(EvolutionListener listener);

    /**
     *
     * @param listener
     */
    void addUpdateListener(UpdateFeed listener);

    /**
     *
     * @return true when fitness should be maximized
     */
    boolean isMaximizedFitness();

    /**
     * Generates colors for newly created clusters
     *
     * @param cg
     */
    void setColorGenerator(ColorGenerator cg);

    /**
     *
     * @return
     */
    ColorGenerator getColorGenerator();

    /**
     *
     * @param ph
     */
    void setProgressHandle(ProgressHandle ph);

    /**
     * Randomly initialized individuals for creating population
     *
     * @return new individual
     */
    I createIndividual();

    /**
     * Default settings of parameter in newly created individual
     *
     * @param key
     * @return
     */
    String getDefaultParam(String key);

    /**
     * Validates individual in context of whole evolution process
     *
     * @param individual
     * @return
     */
    boolean isValid(I individual);

    void fireIndividualCreated(I individual);
}
