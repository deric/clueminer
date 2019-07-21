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
package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalClusterEvaluator;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Cutoff method based on Cophenetic correlation coefficient.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = CutoffStrategy.class)
public class CophCutoff implements CutoffStrategy {

    private static final String NAME = "coph cutoff";
    private final HierarchicalClusterEvaluator eval = new CopheneticCorrelation();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double findCutoff(HierarchicalResult hclust, Props params) {
        Clustering clust, prevClust = null;
        double cutoff;
        double score, prev = Double.NaN, oldcut = 0;
        int level = 1;
        boolean isClimbing = true;
        do {
            cutoff = hclust.cutTreeByLevel(level);
            clust = hclust.getClustering();
            //System.out.println("# level: " + level + ", clust = " + clust + ", cut = " + String.format("%.2f", cutoff));
            score = eval.score(hclust);
            if (cutoff < 0) {
                //System.out.println("negative cutoff " + cutoff + " stopping cutoff");
                isClimbing = false;
            }
            //System.out.println("score = " + score + " prev= " + prev);
            if (!Double.isNaN(prev)) {
                if (score <= prev) {
                    //System.out.println("function is not climbing anymore, reverting to " + oldcut);
                    hclust.setCutoff(oldcut);
                    hclust.setClustering(prevClust);
                    return oldcut;
                }
            }

            prev = score;
            prevClust = clust;
            oldcut = cutoff;
            level++;

        } while (level < (hclust.treeLevels() - 1) && isClimbing && !Double.isNaN(score));
        return cutoff;
    }

    @Override
    public void setEvaluator(InternalEvaluator evaluator) {
        //nothing to do
    }

    @Override
    public boolean isProximityRequired() {
        return true;
    }

}
