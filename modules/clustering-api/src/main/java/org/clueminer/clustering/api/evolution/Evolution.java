package org.clueminer.clustering.api.evolution;

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
 */
public interface Evolution extends Runnable, Lookup.Provider {

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
}
