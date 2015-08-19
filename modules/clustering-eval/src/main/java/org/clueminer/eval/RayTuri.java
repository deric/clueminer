/*
 * Copyright (C) 2011-2015 clueminer.org
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
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @param <E>
 * @param <C>
 * @cite Ray, Siddheswar, and Rose H. Turi. "Determination of number of clusters
 * in k-means clustering and application in colour image segmentation."
 * Proceedings of the 4th international conference on advances in pattern
 * recognition and digital techniques. 1999.
 *
 * @author deric
 */
@ServiceProvider(service = InternalEvaluator.class)
public class RayTuri<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final long serialVersionUID = 6195054290041907628L;
    private static String name = "Ray-Turi";

    public RayTuri() {
        dm = new EuclideanDistance();
    }

    public RayTuri(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double wgss = wgss(clusters);
        double dist;
        Cluster clust;
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            //min squared distance between centroids
            for (int j = 0; j < i; j++) {
                dist = dm.measure(clust.getCentroid(), clusters.get(j).getCentroid());
                dist *= dist;
                if (dist < min) {
                    min = dist;
                }
            }
        }

        return wgss / (clusters.instancesCount() * min);
    }

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

    @Override
    public double getMax() {
        return Double.POSITIVE_INFINITY;
    }

}
