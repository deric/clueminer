package org.clueminer.evolution;

import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.colors.ColorBrewer;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Instance;
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
 * @param <I>
 * @param <E>
 * @param <C>
 */
public abstract class BaseEvolution<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>>
        extends AbstractEvolution<I, E, C> {

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
    protected void evolutionStarted(Evolution<I, E, C> e) {
        fireEvolutionStarts(e);
    }

    @Override
    public void setAlgorithm(ClusteringAlgorithm<E, C> algorithm) {
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

    public ListenerList<EvolutionListener> getEvolutionListeners() {
        return evoListeners;
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

    protected void fireIndividualCreated(I individual) {
        for (UpdateFeed listener : metaListeners) {
            if (listener != null) {
                listener.individualCreated(runId, individual);
            }
        }
    }

    protected void fireBestIndividual(int generationNum, Population<I> population) {
        I best = population.getBestIndividual();
        if (best != null) {
            for (EvolutionListener listener : evoListeners) {
                listener.bestInGeneration(generationNum, population, externalValidation(best));
            }
        }
    }

    protected double externalValidation(I best) {
        if (external != null) {
            return external.score(best.getClustering());
        }
        return Double.NaN;
    }

    protected void fireFinalResult(int g, I best, Pair<Long, Long> time,
            Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness) {

        if (evoListeners != null) {
            for (EvolutionListener listener : evoListeners) {
                listener.finalResult(this, g, best, time, bestFitness, avgFitness, externalValidation(best));
            }
        }
    }

    protected void fireResultUpdate(I[] population) {
        for (EvolutionListener listener : evoListeners) {
            listener.resultUpdate(population);
        }
    }

    /**
     * No validation by default
     *
     * @param individual
     * @return
     */
    @Override
    public boolean isValid(I individual) {
        return individual != null;
    }

}
