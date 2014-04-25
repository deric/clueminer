package org.clueminer.clustering.api.evolution;

import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
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
    public String getName();

    public Dataset<? extends Instance> getDataset();

    public void setDataset(Dataset<? extends Instance> dataset);

    /**
     * Number of attributes in current dataset
     *
     * @return number greater than 0
     */
    public int attributesCount();

    /**
     * Set number of generations in an evolutionary algorithm
     *
     * @param generations
     */
    public void setGenerations(int generations);

    public double getMutationProbability();

    public void setMutationProbability(double mutationProbability);

    public double getCrossoverProbability();

    public void setCrossoverProbability(double crossoverProbability);

    public ClusteringAlgorithm getAlgorithm();

    public void setAlgorithm(ClusteringAlgorithm algorithm);

    public ClusterEvaluation getEvaluator();

    public void setEvaluator(ClusterEvaluation evaluator);

    public ClusterEvaluation getExternal();

    public void setExternal(ClusterEvaluation external);

    public int getPopulationSize();

    public void setPopulationSize(int populationSize);

    public void addEvolutionListener(EvolutionListener listener);

    /**
     *
     * @return true when fitness should be maximized
     */
    public boolean isMaximizedFitness();

    /**
     * Generates colors for newly created clusters
     *
     * @param cg
     */
    public void setColorGenerator(ColorGenerator cg);

    /**
     *
     * @return
     */
    public ColorGenerator getColorGenerator();
}
