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
 * Alternate Simplified Silhoulette index
 *
 * Hruschka, Eduardo R and de Castro, Leandro Nunes and Campello, Ricardo JGB.
 * Evolutionary algorithms for clustering gene-expression data. In Fourth IEEE
 * Inter- national Conference on Data Mining (ICDM’04), IEEE, 2004, pp. 403–406.
 *
 * @author deric
 */
@ServiceProvider(service = InternalEvaluator.class)
public class SilhouetteSimplAlt<E extends Instance, C extends Cluster<E>> extends Silhouette<E, C> {

    private static String NAME = "Silhouette-simpl-alt";
    private static final double EPS = 10e-6;

    public SilhouetteSimplAlt() {
        dm = new EuclideanDistance();
    }

    public SilhouetteSimplAlt(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double instanceScore(C clust, Clustering<E, C> clusters, int i, E x) {
        double a, b;

        //find minimal distance to other clusters
        b = minDistance(x, clusters, i);
        //distance to a centroid
        a = dm.measure(x, clust.getCentroid());
        return b / (a + EPS);
    }

}
