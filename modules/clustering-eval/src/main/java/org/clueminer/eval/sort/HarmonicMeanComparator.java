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

        for (int i = 0; i < objectives.size(); i++) {
            score += 1.0 / normalize(et, i);
        }
        return objectives.size() / score;
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
