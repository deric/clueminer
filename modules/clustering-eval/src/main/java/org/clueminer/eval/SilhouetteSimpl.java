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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.openide.util.lookup.ServiceProvider;

/**
 * Simplified Silhoulette index
 *
 * Hruschka, Eduardo R and de Castro, Leandro Nunes and Campello, Ricardo JGB.
 * Evolutionary algorithms for clustering gene-expression data. In Fourth IEEE
 * Inter- national Conference on Data Mining (ICDM’04), IEEE, 2004, pp. 403–406.
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = InternalEvaluator.class)
public class SilhouetteSimpl<E extends Instance, C extends Cluster<E>> extends Silhouette<E, C> {

    private static String NAME = "Silhouette-simpl";
    private static String CALLSIGN = "ss";
    private static final long serialVersionUID = 2679542818862912390L;

    public SilhouetteSimpl() {
        dm = new EuclideanDistance();
    }

    public SilhouetteSimpl(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCallsign() {
        return CALLSIGN;
    }

    /**
     * Instead of computing average distance in cluster, distance to centroid is
     * used
     *
     * @param clust
     * @param clusters
     * @param i index of cluster
     * @param x
     * @return
     */
    @Override
    public double instanceScore(C clust, Clustering<E, C> clusters, int i, E x) {
        double a, b, denom;

        //find minimal distance to other clusters
        b = minDistance(x, clusters, i);
        //distance to a centroid
        a = dm.measure(x, clust.getCentroid());
        denom = Math.max(b, a);
        //avoid NaN, if possible
        if (denom == 0.0) {
            return 0.0;
        }
        return (b - a) / denom;
    }

}
