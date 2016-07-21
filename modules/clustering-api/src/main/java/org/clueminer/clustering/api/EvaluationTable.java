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
package org.clueminer.clustering.api;

import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * Table with evaluation results, might perform caching or other optimizations
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface EvaluationTable<E extends Instance, C extends Cluster<E>> {

    void setData(Clustering<E, C> clusters, Dataset<E> dataset);

    /**
     * Get score for given evaluator
     *
     * @param evaluator
     * @return
     */
    double getScore(ClusterEvaluation<E, C> evaluator);

    /**
     *
     * @param evaluator
     * @param params    optional parameters of evaluation metric
     * @return
     */
    double getScore(ClusterEvaluation<E, C> evaluator, Props params);

    /**
     * Get score for given evaluator
     *
     * @param evaluator name of the evaluator
     * @return
     */
    double getScore(String evaluator);

    /**
     *
     * @param evaluator
     * @param params    optional parameters of evaluation metric
     * @return
     */
    double getScore(String evaluator, Props params);

    /**
     * Names of all available evaluators
     *
     * @return
     */
    String[] getEvaluators();

    /**
     *
     * @return map of internal evaluation scores
     */
    Map<String, Double> getInternal();

    /**
     *
     * @return map of external evaluations scores
     */
    Map<String, Double> getExternal();

    /**
     *
     * @return all currently computed scores
     */
    Map<String, Double> getAll();

    /**
     * Count score that have not been evaluated so far
     *
     * @return map with all scores
     */
    Map<String, Double> countAll();
}
