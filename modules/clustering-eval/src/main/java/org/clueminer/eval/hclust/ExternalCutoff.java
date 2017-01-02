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
package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.eval.external.NMIsqrt;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Fake cutoff based on the external evaluation.
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = CutoffStrategy.class)
public class ExternalCutoff implements CutoffStrategy {

    private static final String name = "External_cutoff";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double findCutoff(HierarchicalResult hclust, Props params) {
        double bestScore = Double.MIN_VALUE;
        NMIsqrt evaluator = new NMIsqrt();
        double cutoff = 0;
        for (int i = 0; i <= hclust.treeLevels(); i++) {
            double height = hclust.cutTreeByLevel(i);
            double score;
            try {
                score = evaluator.score(hclust.getClustering());
            } catch (ScoreException ex) {
                score = Double.NEGATIVE_INFINITY;
            }
            if (score > bestScore) {
                bestScore = score;
                cutoff = height;
            }
        }
        return cutoff;
    }

    @Override
    public void setEvaluator(InternalEvaluator evaluator) {
        // nothing to do
    }

}
