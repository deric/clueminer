package org.clueminer.evolution;

import javax.swing.event.EventListenerList;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.clustering.api.evolution.EvolutionListener;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.api.evolution.Pair;
import org.clueminer.colors.ColorBrewer;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractEvolution implements Evolution {

    protected int generations = 100;
    protected ColorGenerator cg = new ColorBrewer();
    protected ClusterEvaluation external;
    protected ClusteringAlgorithm algorithm;
    protected boolean maximizedFitness;
    protected ProgressHandle ph;
    protected transient InstanceContent instanceContent;
    protected transient Lookup lookup;
    protected Dataset<? extends Instance> dataset;
    protected ClusterEvaluation evaluator;
    protected int populationSize = 100;
    /**
     * Probability of mutation
     */
    protected double mutationProbability = 0.3;
    /**
     * Probability of crossover
     */
    protected double crossoverProbability = 0.3;

    protected final transient EventListenerList evoListeners = new EventListenerList();

    @Override
    public boolean isMaximizedFitness() {
        return maximizedFitness;
    }

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
    public void setColorGenerator(ColorGenerator cg) {
        this.cg = cg;

    }

    @Override
    public ColorGenerator getColorGenerator() {
        return cg;
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
    public void setAlgorithm(ClusteringAlgorithm algorithm) {
        this.algorithm = algorithm;
        if (cg != null) {
            algorithm.setColorGenerator(cg);
        }
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
    public void addEvolutionListener(EvolutionListener listener) {
        evoListeners.add(EvolutionListener.class, listener);
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
        maximizedFitness = evaluator.compareScore(1.0, 0.0);
    }

    protected void fireBestIndividual(int generationNum, Individual best, double avgFitness) {
        for (EvolutionListener listener : evoListeners.getListeners(EvolutionListener.class)) {
            listener.bestInGeneration(generationNum, best, avgFitness, externalValidation(best));
        }
    }

    protected double externalValidation(Individual best) {
        if (external != null) {
            return external.score(best.getClustering(), dataset);
        }
        return Double.NaN;
    }

    protected void fireFinalResult(int g, Individual best, Pair<Long, Long> time,
            Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness) {

        if (evoListeners != null) {
            for (EvolutionListener listener : evoListeners.getListeners(EvolutionListener.class)) {
                listener.finalResult(this, g, best, time, bestFitness, avgFitness, externalValidation(best));
            }
        }
    }

}
