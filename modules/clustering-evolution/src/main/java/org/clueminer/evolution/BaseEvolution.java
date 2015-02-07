package org.clueminer.evolution;

import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.colors.ColorBrewer;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.events.ListenerList;
import org.clueminer.evolution.api.AbstractEvolution;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionListener;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Pair;
import org.clueminer.evolution.api.Population;
import org.clueminer.evolution.api.UpdateFeed;

/**
 *
 * @author Tomas Barton
 * @param <T>
 */
public abstract class BaseEvolution<T extends Individual> extends AbstractEvolution<T> {

    protected ColorGenerator cg = new ColorBrewer();
    protected ObjectOpenCustomHashSet<Clustering> uniqueClusterings = new ObjectOpenCustomHashSet<>(new ClustHash<>());

    protected final transient ListenerList<EvolutionListener> evoListeners = new ListenerList<>();
    protected final transient ListenerList<UpdateFeed> metaListeners = new ListenerList<>();
    /**
     * for storing meta-data
     */
    protected int runId;

    /**
     * Hook that should be called when evolution starts
     *
     * @param e
     */
    protected void evolutionStarted(Evolution e) {
        fireEvolutionStarts(e);
    }

    @Override
    public void setAlgorithm(ClusteringAlgorithm algorithm) {
        this.algorithm = algorithm;
        if (cg != null) {
            algorithm.setColorGenerator(cg);
        }
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
    public void addEvolutionListener(EvolutionListener listener) {
        evoListeners.add(listener);
    }

    @Override
    public void addUpdateListener(UpdateFeed listener) {
        metaListeners.add(listener);
    }

    protected void fireEvolutionStarts(Evolution e) {
        for (EvolutionListener listener : evoListeners) {
            if (listener != null) {
                listener.started(e);
            }
        }
        for (UpdateFeed listener : metaListeners) {
            if (listener != null) {
                //TODO: doesn't work for multiple storages
                runId = listener.started(e);
            }
        }
    }

    protected void fireIndividualCreated(Individual individual) {
        for (UpdateFeed listener : metaListeners) {
            if (listener != null) {
                listener.individualCreated(runId, individual);
            }
        }
    }

    protected void fireBestIndividual(int generationNum, Population<? extends Individual> population) {
        Individual best = population.getBestIndividual();
        if (best != null) {
            for (EvolutionListener listener : evoListeners) {
                listener.bestInGeneration(generationNum, population, externalValidation(best));
            }
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
            for (EvolutionListener listener : evoListeners) {
                listener.finalResult(this, g, best, time, bestFitness, avgFitness, externalValidation(best));
            }
        }
    }

    protected void fireResultUpdate(Individual[] population) {
        for (EvolutionListener listener : evoListeners) {
            listener.resultUpdate(population);
        }
    }

}
