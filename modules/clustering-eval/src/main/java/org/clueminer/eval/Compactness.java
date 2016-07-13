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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Compactness should be used together with other criteria (multi-objective
 * optimization). The criteria should be minimized as it is computed as an
 * average of pairwise distances in each cluster.
 *
 * @author deric
 * @param <E>
 * @param <C>
 *
 * @see Caruana, Rich, et al. "Meta clustering." Data Mining, 2006. ICDM'06.
 * Sixth International Conference on. IEEE, 2006.
 */
@ServiceProvider(service = InternalEvaluator.class)
public class Compactness<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final String NAME = "Compactness";
    private static final long serialVersionUID = -6033217683756447290L;

    public Compactness() {
        dm = EuclideanDistance.getInstance();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double sum = 0;
        Cluster<E> clust;
        double dist;
        int instCnt = 0;
        E a, b;
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            dist = 0;
            // we can't compute compactness for singleton clusters (DIV by ZERO)
            if (clust.size() > 1) {
                for (int j = 0; j < clust.size(); j++) {
                    a = clust.get(j);
                    for (int k = 0; k < j; k++) {
                        if (j != k) {
                            b = clust.get(k);
                            dist += dm.measure(a, b);
                        }
                    }
                }
                //n_k * sum(..) / (n_k (n_k - 1) / 2)
                // a micro-algebraic optimization
                sum += 2 * dist / (clust.size() - 1);
                instCnt += clust.size();
            }
        }
        return sum / instCnt;
    }

    /**
     * Should be minimized
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        return score1 < score2;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

    @Override
    public double getMin() {
        return 0;
    }

    /**
     * Best value
     *
     * @return
     */
    @Override
    public double getMax() {
        return 0;
    }

}
