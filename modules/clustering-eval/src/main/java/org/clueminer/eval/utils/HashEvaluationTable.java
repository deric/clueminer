/*
 * Copyright (C) 2011-2019 clueminer.org
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
package org.clueminer.eval.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.clustering.api.factory.ExternalEvaluatorFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Storage for evaluation metrics which are frequently very expensive to
 * compute.
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class HashEvaluationTable<E extends Instance, C extends Cluster<E>> implements EvaluationTable<E, C> {

    private Clustering<E, C> clustering;
    protected static Object2ObjectMap<String, ClusterEvaluation> internalMap;
    protected static Object2ObjectMap<String, ClusterEvaluation> externalMap;
    private ReentrantLock initLock = new ReentrantLock();
    private TreeMap<String, Double> scores;
    private static final Logger LOG = LoggerFactory.getLogger(HashEvaluationTable.class);

    public HashEvaluationTable(Clustering<E, C> clustering, Dataset<E> dataset) {
        initEvaluators();
        setData(clustering, dataset);
    }

    @Override
    public final void setData(Clustering<E, C> clustering, Dataset<E> dataset) {
        this.clustering = (Clustering<E, C>) clustering;
        reset();
    }

    private void reset() {
        scores = new TreeMap<>();
    }

    @Override
    public SortedMap<String, Double> getAll() {
        return scores;
    }

    @Override
    public Map<String, Double> countAll() {
        for (ClusterEvaluation<E, C> eval : internalMap.values()) {
            getScore(eval);
        }
        for (ClusterEvaluation<E, C> eval : externalMap.values()) {
            getScore(eval);
        }
        return getAll();
    }

    @Override
    public double getScore(ClusterEvaluation<E, C> evaluator, Props params) {
        String key = evaluator.getName();
        if (scores.containsKey(key)) {
            return scores.get(key);
        } else {
            double score;
            try {
                score = evaluator.score(clustering, params);
            } catch (ArithmeticException | ScoreException ex) {
                LOG.warn("failed to compute score {}: {}",
                        evaluator.getName(), ex.getMessage(), ex);
                score = Double.NaN;
            }
            scores.put(key, score);
            return score;
        }
    }

    @Override
    public double getScore(String evaluator, Props params) {
        if (initLock.isLocked()) {
            try {
                initLock.wait();
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        if (internalMap.containsKey(evaluator)) {
            return getScore(internalMap.get(evaluator), params);
        } else if (externalMap.containsKey(evaluator)) {
            return getScore(externalMap.get(evaluator), params);
        } else {
            throw new RuntimeException("unknown evaluator");
        }
    }

    /**
     * Computes evaluator score and caches the result
     *
     * @param evaluator
     * @return
     */
    @Override
    public double getScore(ClusterEvaluation evaluator) {
        return getScore(evaluator, new Props());
    }

    @Override
    public double getScore(String evaluator) {
        return getScore(evaluator, new Props());
    }

    private Map<String, Double> evalToScoreMap(Object2ObjectMap<String, ClusterEvaluation> map) {
        TreeMap<String, Double> res = new TreeMap<>();
        for (ClusterEvaluation<E, C> eval : map.values()) {
            res.put(eval.getName(), getScore(eval));
        }
        return res;
    }

    @Override
    public synchronized Map<String, Double> getInternal() {
        if (initLock.isLocked()) {
            try {
                initLock.wait();
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return evalToScoreMap(internalMap);
    }

    @Override
    public Map<String, Double> getExternal() {
        if (initLock.isLocked()) {
            try {
                initLock.wait();
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return evalToScoreMap(externalMap);
    }

    @Override
    public String[] getEvaluators() {
        if (internalMap == null) {
            return new String[0];
        }
        Collection<String> evaluators = new TreeSet<>();
        evaluators.addAll(internalMap.keySet());
        evaluators.addAll(externalMap.keySet());
        return evaluators.toArray(new String[internalMap.size()]);
    }

    private void initEvaluators() {
        if (internalMap == null || externalMap == null) {
            initLock.lock();
            InternalEvaluatorFactory<E, C> inf = InternalEvaluatorFactory.getInstance();
            internalMap = new Object2ObjectOpenHashMap<>();

            for (InternalEvaluator<E, C> eval : inf.getAll()) {
                internalMap.put(eval.getName(), eval);
            }

            externalMap = new Object2ObjectOpenHashMap<>();
            ExternalEvaluatorFactory extf = ExternalEvaluatorFactory.getInstance();
            for (ExternalEvaluator eval : extf.getAll()) {
                externalMap.put(eval.getName(), eval);
            }
            initLock.unlock();
        }
    }

}
