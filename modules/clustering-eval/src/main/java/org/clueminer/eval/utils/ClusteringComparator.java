/*
 * Copyright (C) 2011-2017 clueminer.org
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

import java.util.Comparator;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 * Compares clustering based on objective given (internal or external one).
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class ClusteringComparator<E extends Instance, C extends Cluster<E>> implements Comparator<Clustering<E, C>> {

    private ClusterEvaluation evaluator;

    public ClusteringComparator() {

    }

    public ClusteringComparator(ClusterEvaluation<E, C> eval) {
        this.evaluator = eval;
    }

    @Override
    public int compare(Clustering<E, C> c1, Clustering<E, C> c2) {
        EvaluationTable t1 = evaluationTable(c1);
        EvaluationTable t2 = evaluationTable(c2);

        double s1 = t1.getScore(evaluator);
        double s2 = t2.getScore(evaluator);
        return evaluator.compare(s1, s2);
    }

    public EvaluationTable evaluationTable(Clustering<E, C> clustering) {
        EvaluationTable<E, C> evalTable = clustering.getLookup().lookup(EvaluationTable.class);
        //we try to compute score just once, to eliminate delays
        if (evalTable == null) {
            Dataset<E> dataset = clustering.getLookup().lookup(Dataset.class);
            if (dataset == null) {
                throw new RuntimeException("no dataset in lookup");
            }
            evalTable = new HashEvaluationTable<>(clustering, dataset);
            clustering.lookupAdd(evalTable);
        }
        return evalTable;
    }

    /**
     * Searches for cached score, if missing computes score and stores it into
     * the cache
     *
     * @param clustering
     * @return score of given clustering with current evaluator
     */
    public double getScore(Clustering<E, C> clustering) {
        return evaluationTable(clustering).getScore(evaluator);
    }

    public ClusterEvaluation getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(ClusterEvaluation evaluator) {
        this.evaluator = evaluator;
    }

}
