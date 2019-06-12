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
package org.clueminer.eval.sort;

import java.util.Comparator;
import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.HashEvaluationTable;
import org.clueminer.math.Matrix;
import org.clueminer.std.StdScale;
import org.clueminer.utils.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Computes mean of given objectives
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class MeanComparator<E extends Instance, C extends Cluster<E>> implements Comparator<Clustering<E, C>>, ClusterEvaluation<E, C> {

    private List<ClusterEvaluation<E, C>> objectives;

    private double[] min;
    private double[] max;
    private double[] mean;
    public static final double SCORE_MIN = 0.0;
    //maximum value of each objective after scaling
    public static final double SCORE_MAX = 10.0;
    private final StdScale scale = new StdScale();
    private static final Logger LOG = LoggerFactory.getLogger(MeanComparator.class);

    public MeanComparator(List<ClusterEvaluation<E, C>> objectives) {
        setObjectives(objectives);
    }

    @Override
    public int compare(Clustering<E, C> c1, Clustering<E, C> c2) {
        double s1 = aggregatedScore(c1);
        double s2 = aggregatedScore(c2);

        return compare(s1, s2);
    }

    public double aggregatedScore(Clustering<E, C> clust) {
        EvaluationTable et = evaluationTable(clust);
        double score = 0.0;
        double value, sc;
        ClusterEvaluation<E, C> eval;

        for (int i = 0; i < objectives.size(); i++) {
            eval = objectives.get(i);
            value = et.getScore(eval);
            //replace NaNs by worst known value
            if (!Double.isFinite(value)) {
                if (eval.isMaximized()) {
                    value = min[i];
                } else {
                    value = max[i];
                }
            }
            //scale score to scale [0,10]
            if (eval.isMaximized()) {
                //flip value
                sc = scale.scaleToRange(value, min[i], max[i], SCORE_MAX, SCORE_MIN);
            } else {
                sc = scale.scaleToRange(value, min[i], max[i], SCORE_MIN, SCORE_MAX);
            }
            LOG.debug("{}: {}", eval.getName(), sc);
            score += sc;

        }
        return score;
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
    public double getScore(Clustering<E, C> clustering, ClusterEvaluation<E, C> evaluator) {
        return evaluationTable(clustering).getScore(evaluator);
    }

    public List<ClusterEvaluation<E, C>> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<ClusterEvaluation<E, C>> objectives) {
        this.objectives = objectives;
    }

    public void updateStats(Clustering<E, C>[] clusterings) {
        if (objectives != null) {
            min = new double[objectives.size()];
            max = new double[objectives.size()];
            mean = new double[objectives.size()];
            for (int i = 0; i < objectives.size(); i++) {
                min[i] = Double.MAX_VALUE;
                max[i] = Double.MIN_VALUE;
                mean[i] = 0.0;
            }
            double score;
            for (Clustering<E, C> clust : clusterings) {
                for (int i = 0; i < objectives.size(); i++) {
                    score = getScore(clust, objectives.get(i));
                    if (score < min[i]) {
                        min[i] = score;
                    }
                    if (score > max[i]) {
                        max[i] = score;
                    }
                    mean[i] += score;
                }
            }
            //compute mean
            for (int i = 0; i < objectives.size(); i++) {
                mean[i] /= clusterings.length;
                //System.out.println(objectives.get(i).getName() + ", min: " + min[i] + ", max: " + max[i] + ", mean: " + mean[i]);
            }
        } else {
            throw new RuntimeException("no objectives were set");
        }
    }

    @Override
    public String getName() {
        return "mean score (" + printObjectives() + ")";
    }

    private String printObjectives() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (ClusterEvaluation ce : objectives) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(ce.getName());
            i++;
        }
        return sb.toString();
    }

    @Override
    public String getHandle() {
        String h = getName().toLowerCase();
        h = h.replace(" ", "_"); //space
        h = h.replace("-", "_");
        h = h.replace("+", "_");
        h = h.replace("&", "_");
        return h;
    }

    @Override
    public double score(Clustering<E, C> clusters) throws ScoreException {
        return aggregatedScore(clusters);
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) throws ScoreException {
        return score(clusters);
    }

    @Override
    public double score(Clustering<E, C> clusters, Matrix proximity, Props params) throws ScoreException {
        return score(clusters);
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        return score1 < score2;
    }

    @Override
    public int compare(double s1, double s2) {
        //minimize, smaller is better
        if (s1 < s2) {
            return 1;
        } else if (s1 == s2) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public boolean isExternal() {
        return true;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

    @Override
    public double getMin() {
        return SCORE_MIN;
    }

    @Override
    public double getMax() {
        return objectives.size() * SCORE_MAX;
    }

}
