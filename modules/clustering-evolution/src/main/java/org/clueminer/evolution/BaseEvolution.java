/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.evolution;

import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.StdStorage;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.colors.ColorBrewer;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.events.ListenerList;
import org.clueminer.evolution.api.AbstractEvolution;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionListener;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Pair;
import org.clueminer.evolution.api.Population;
import org.clueminer.evolution.api.UpdateFeed;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
import org.clueminer.utils.Props;

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

    private static final Logger LOGGER = Logger.getLogger(BaseEvolution.class.getName());
    /**
     * for storing meta-data
     */
    protected int runId;
    protected StdStorage stdStore;

    public BaseEvolution() {
        super();
    }

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
            try {
                return external.score(best.getClustering());
            } catch (ScoreException ex) {
                LOGGER.log(Level.WARNING, "failed to computer {0}: {1}",
                        new Object[]{external.getName(), ex.getMessage()});
            }
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

    protected void prepare() {
        if (dataset == null) {
            throw new RuntimeException("missing data");
        }
        stdStore = new StdStorage(dataset);
    }

    public Matrix standartize(Dataset<E> data, String method, boolean logScale) {
        return Scaler.standartize(data.arrayCopy(), method, logScale);
    }

    public Dataset<E> standartize(Props params) {
        return stdStore.get(params.get(AlgParams.STD, Scaler.NONE), params.getBoolean(AlgParams.LOG, false));
    }

    protected void finish() {
        if (ph != null) {
            ph.finish();
        }
    }

}
