package org.clueminer.evolution.api;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Tomas Barton
 * @param <I>
 * @param <E>
 * @param <C>
 */
public abstract class AbstractEvolution<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>>
        implements EvolutionSO<I, E, C> {

    protected int generations = 10;
    protected ClusterEvaluation<E, C> external;
    protected ClusteringAlgorithm<E, C> algorithm;
    protected boolean maximizedFitness;
    protected ProgressHandle ph;
    protected transient InstanceContent instanceContent;
    protected transient Lookup lookup;
    protected Dataset<E> dataset;
    protected ClusterEvaluation<E, C> evaluator;
    protected int populationSize = 10;
    protected Props defaultProp;
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
    public Dataset<E> getDataset() {
        return dataset;
    }

    @Override
    public void setDataset(Dataset<E> dataset) {
        this.dataset = dataset;
    }

    @Override
    public int attributesCount() {
        return dataset.attributeCount();
    }

    @Override
    public ClusteringAlgorithm<E, C> getAlgorithm() {
        return algorithm;
    }

    @Override
    public ClusterEvaluation<E, C> getEvaluator() {
        return evaluator;
    }

    /**
     * External validation criterion, is used only for reporting, not during
     * evolution
     *
     * @return
     */
    @Override
    public ClusterEvaluation<E, C> getExternal() {
        return external;
    }

    @Override
    public void setExternal(ClusterEvaluation<E, C> external) {
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
        I ind = createIndividual();
        return ind.getProps().get(key);
    }

    @Override
    public void setDefaultProps(Props prop) {
        this.defaultProp = prop;
    }

    @Override
    public Props getDefaultProps() {
        if (defaultProp == null) {
            return new Props();
        }
        return this.defaultProp.copy();
    }
}
