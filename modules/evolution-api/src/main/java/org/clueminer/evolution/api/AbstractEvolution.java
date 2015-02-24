package org.clueminer.evolution.api;

import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Tomas Barton
 * @param <T>
 */
public abstract class AbstractEvolution<T extends Individual> implements EvolutionSO<T> {

    protected int generations = 10;
    protected ClusterEvaluation external;
    protected ClusteringAlgorithm algorithm;
    protected boolean maximizedFitness;
    protected ProgressHandle ph;
    protected transient InstanceContent instanceContent;
    protected transient Lookup lookup;
    protected Dataset<? extends Instance> dataset;
    protected ClusterEvaluation evaluator;
    protected int populationSize = 10;
    /**
     * parameter for clustering counting same solutions
     */
    protected static String NUM_OCCUR = "num_occur";
    /**
     * Probability of mutation
     */
    protected double mutationProbability = 0.3;
    /**
     * Probability of crossover
     */
    protected double crossoverProbability = 0.3;

    @Override
    public boolean isMaximizedFitness() {
        return maximizedFitness;
    }

    @Override
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

    @Override
    public void setProgressHandle(ProgressHandle ph) {
        this.ph = ph;
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
    public int attributesCount() {
        return dataset.attributeCount();
    }

    @Override
    public ClusteringAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public ClusterEvaluation getEvaluator() {
        return evaluator;
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
    public int getPopulationSize() {
        return populationSize;
    }

    @Override
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    @Override
    public void setEvaluator(ClusterEvaluation evaluator) {
        this.evaluator = evaluator;
        maximizedFitness = evaluator.isMaximized();
    }

    @Override
    public String getDefaultParam(String key) {
        T ind = createIndividual();
        return ind.getProps().get(key);
    }
}
