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
package org.clueminer.eval.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
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

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class HashEvaluationTable<E extends Instance, C extends Cluster<E>> implements EvaluationTable<E, C> {

    private Clustering<E, C> clustering;
    protected static Object2ObjectMap<String, ClusterEvaluation> internalMap;
    protected static Object2ObjectMap<String, ClusterEvaluation> externalMap;
    private HashMap<String, Double> scores;
    private static final Logger LOGGER = Logger.getLogger(HashEvaluationTable.class.getName());

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
        scores = new HashMap<>(internalMap.size() + externalMap.size());
    }

    @Override
    public HashMap<String, Double> getAll() {
        return scores;
    }

    @Override
    public HashMap<String, Double> countAll() {
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
            } catch (ScoreException ex) {
                LOGGER.log(Level.WARNING, "failed to compute score {0}: {1}",
                        new Object[]{evaluator.getName(), ex.getMessage()});
                score = Double.NaN;
            }
            scores.put(key, score);
            return score;
        }
    }

    @Override
    public double getScore(String evaluator, Props params) {
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
        HashMap<String, Double> res = new HashMap<>(map.size());
        for (ClusterEvaluation eval : map.values()) {
            res.put(eval.getName(), getScore(eval));
        }
        return res;
    }

    @Override
    public Map<String, Double> getInternal() {
        return evalToScoreMap(internalMap);
    }

    @Override
    public Map<String, Double> getExternal() {
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
        if (internalMap == null) {
            InternalEvaluatorFactory<E, C> inf = InternalEvaluatorFactory.getInstance();
            internalMap = new Object2ObjectOpenHashMap<>();

            for (InternalEvaluator<E, C> eval : inf.getAll()) {
                internalMap.put(eval.getName(), eval);
            }
        }
        if (externalMap == null) {
            externalMap = new Object2ObjectOpenHashMap<>();
            ExternalEvaluatorFactory extf = ExternalEvaluatorFactory.getInstance();
            for (ExternalEvaluator eval : extf.getAll()) {
                externalMap.put(eval.getName(), eval);
            }
        }
    }

}
