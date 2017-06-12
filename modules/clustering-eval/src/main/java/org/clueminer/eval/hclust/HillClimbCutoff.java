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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = CutoffStrategy.class)
public class HillClimbCutoff implements CutoffStrategy {

    protected InternalEvaluator evaluator;
    private static final String NAME = "hill-climb cutoff";
    private static final Logger LOGGER = Logger.getLogger(HillClimbCutoff.class.getName());

    public HillClimbCutoff() {
        //evaluator must be set after calling constructor!
    }

    public HillClimbCutoff(InternalEvaluator eval) {
        this.evaluator = eval;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double findCutoff(HierarchicalResult hclust, Props params) {
        check();
        double cutoff;
        Clustering clust, prevClust = null;
        double score, prev = Double.NaN, oldcut = 0;
        int level = hclust.treeLevels() - 1;
        boolean isClimbing = true;
        String evalName;
        int clustNum;
        do {
            cutoff = hclust.cutTreeByLevel(level);
            clust = hclust.getClustering();
            //System.out.println("level: " + level + ", clust = " + clust + ", cut = " + String.format("%.2f", cutoff));
            evalName = evaluator.getName();
            clustNum = clust.size();
            if (hclust.isScoreCached(evalName, clustNum)) {
                score = hclust.getScore(evalName, clustNum);
            } else {
                try {
                    score = evaluator.score(clust, params);
                } catch (ScoreException ex) {
                    LOGGER.log(Level.WARNING, "failed to computer score{0}", ex.getMessage());
                    return Double.NaN;
                }
            }
            //System.out.println("score = " + score + " prev= " + prev);
            hclust.setScores(evaluator.getName(), clust.size(), score);
            if (!Double.isNaN(prev)) {
                if (!evaluator.isBetter(score, prev)) {
                    //System.out.println("function is not climbing anymore, reverting");
                    hclust.setCutoff(oldcut);
                    hclust.setClustering(prevClust);
                    return oldcut;
                }
            }
            prev = score;
            prevClust = clust;
            oldcut = cutoff;
            level--;

        } while (isClimbing && !Double.isNaN(score));
        return cutoff;
    }

    public InternalEvaluator getEvaluator() {
        return evaluator;
    }

    @Override
    public void setEvaluator(InternalEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    protected void check() {
        if (evaluator == null) {
            throw new RuntimeException("evaluator method must be set!");
        }
    }

    @Override
    public boolean isProximityRequired() {
        return false;
    }
}
