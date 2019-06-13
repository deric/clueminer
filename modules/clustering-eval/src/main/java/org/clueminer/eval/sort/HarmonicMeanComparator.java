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

import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.dataset.api.Instance;
import static org.clueminer.eval.sort.MeanComparator.SCORE_MAX;
import static org.clueminer.eval.sort.MeanComparator.SCORE_MIN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class HarmonicMeanComparator<E extends Instance, C extends Cluster<E>> extends MeanComparator<E, C> {

    private static final Logger LOG = LoggerFactory.getLogger(HarmonicMeanComparator.class);

    public HarmonicMeanComparator(List<ClusterEvaluation<E, C>> objectives) {
        super(objectives);
    }

    @Override
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
            //scale score to scale [1,10]
            if (eval.isMaximized()) {
                //flip value
                sc = scale.scaleToRange(value, min[i], max[i], SCORE_MAX, SCORE_MIN);
            } else {
                sc = scale.scaleToRange(value, min[i], max[i], SCORE_MIN, SCORE_MAX);
            }
            //LOG.debug("{}: {}", eval.getName(), sc);
            score += 1.0 / sc;

        }
        return objectives.size() / score;
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
            LOG.error("no objectives were set");
        }
    }

    @Override
    public String getName() {
        return "harmonic mean (" + printObjectives() + ")";
    }

    @Override
    public double getMin() {
        return SCORE_MIN;
    }

    @Override
    public double getMax() {
        return SCORE_MAX;
    }

}
