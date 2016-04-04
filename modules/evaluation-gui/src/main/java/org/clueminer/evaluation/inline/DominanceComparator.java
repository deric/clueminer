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
package org.clueminer.evaluation.inline;

import java.util.Comparator;
import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.HashEvaluationTable;

/**
 * Compares two clusterings using multiple objectives
 *
 * @author deric
 */
public class DominanceComparator<E extends Instance, C extends Cluster<E>> implements Comparator<Clustering<E, C>> {

    private final double epsilon = 1e-9;
    private final List<ClusterEvaluation> objectives;

    public DominanceComparator(List<ClusterEvaluation> objectives) {
        this.objectives = objectives;
    }

    /**
     * A comparator with reversed sorting logic
     *
     * @param c1
     * @param c2
     * @return
     */
    @Override
    public int compare(Clustering c1, Clustering c2) {
        boolean solution1Dominates = false;
        boolean solution2Dominates = false;

        int flag;
        double value1, value2;
        double diff;
        for (ClusterEvaluation objective : objectives) {
            value1 = score(c1, objective);
            value2 = score(c2, objective);

            diff = value1 - value2;
            if (Math.abs(diff) <= epsilon) {
                //same with epsilon tolerance
                flag = 0;
            } else {
                if (objective.isMaximized()) {
                    //maximize objective
                    if (diff > 0.0) {
                        //solution 1 dominates
                        flag = -1;
                    } else {
                        flag = 1;
                    }
                } else {
                    //minimize objective
                    if (diff > 0.0) {
                        //solution 1 dominates
                        flag = 1;
                    } else {
                        flag = -1;
                    }
                }
            }

            if (flag == -1) {
                solution1Dominates = true;
            }

            if (flag == 1) {
                solution2Dominates = true;
            }
        }
        return dominance2int(solution1Dominates, solution2Dominates);
    }

    public double score(Clustering clust, ClusterEvaluation eval) {
        if (clust == null) {
            return Double.NaN;
        }
        EvaluationTable et = evaluationTable(clust);
        return et.getScore(eval);
    }

    public EvaluationTable evaluationTable(Clustering<E, C> clustering) {
        EvaluationTable evalTable = clustering.getEvaluationTable();
        //we try to compute score just once, to eliminate delays
        if (evalTable == null) {
            Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
            if (dataset == null) {
                throw new RuntimeException("no dataset associated with clustering");
            }
            evalTable = new HashEvaluationTable(clustering, dataset);
            clustering.setEvaluationTable(evalTable);
        }
        return evalTable;
    }

    private int dominance2int(boolean solution1Dominates, boolean solution2Dominates) {
        int result;
        if (solution1Dominates == solution2Dominates) {
            // non-dominated solutions
            result = 0;
        } else if (solution1Dominates) {
            // solution1 dominates
            result = -1;
        } else {
            // solution2 dominates
            result = 1;
        }
        return result;
    }
}
