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
package org.clueminer.eval.external;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.eval.utils.PairMatch;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public abstract class AbstractCountingPairs<E extends Instance, C extends Cluster<E>> extends AbstractExternalEval<E, C> {

    private static final long serialVersionUID = -8708340302697665494L;

    public abstract double countScore(PairMatch pm, Props params);

    @Override
    public double score(Clustering<E, C> clusters, Matrix proximity, Props params) throws ScoreException {
        return score(clusters, params);
    }

    @Override
    public double score(Clustering<E, C> clusters) throws ScoreException {
        return score(clusters, new Props());
    }

    public String getHandle() {
        String h = getName().toLowerCase();
        h = h.replace(" ", "_"); //space
        h = h.replace("-", "_");
        h = h.replace("+", "_");
        return h;
    }

    /**
     * Once matching classes <-> clusters are found result will be stored in
     * clustering lookup
     *
     * @param clusters
     * @param params
     * @return
     */
    @Override
    public double score(Clustering<E, C> clusters, Props params) throws ScoreException {
        PairMatch pm = clusters.getLookup().lookup(PairMatch.class);
        //we don't expect mapping to original to change, so we can store the result
        if (pm == null) {
            pm = CountingPairs.getInstance().matchPairs(clusters);
            if (pm == null) {
                //no labels present in the dataset
                return Double.NaN;
            }
            clusters.lookupAdd(pm);
        }
        return countScore(pm, params);
    }

    @Override
    public double score(Clustering<E, C> c1, Clustering<E, C> c2, Props params) {
        PairMatch pm = CountingPairs.getInstance().matchPairs(c1, c2);
        return countScore(pm, params);
    }

    /**
     * Bigger is better
     *
     * @return
     */
    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public double getMin() {
        return 0;
    }

    @Override
    public double getMax() {
        return 1;
    }
}
