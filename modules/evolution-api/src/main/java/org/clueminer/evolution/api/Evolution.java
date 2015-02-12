package org.clueminer.evolution.api;

import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 * @param <T>
 */
public interface Evolution<T extends Individual> extends Runnable, Lookup.Provider {

    /**
     *
     * @return unique identification of algorithm
     */
    String getName();

    Dataset<? extends Instance> getDataset();

    void setDataset(Dataset<? extends Instance> dataset);

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

    ClusteringAlgorithm getAlgorithm();

    void setAlgorithm(ClusteringAlgorithm algorithm);

    ClusterEvaluation getEvaluator();

    void setEvaluator(ClusterEvaluation evaluator);

    ClusterEvaluation getExternal();

    void setExternal(ClusterEvaluation external);

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
    T createIndividual();

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
    boolean isValid(Individual individual);
}
