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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.LinkageFactory;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Dunn's index should be maximized
 *
 * @param <E>
 * @param <C>
 * @cite J. Dunn. Well separated clusters and optimal fuzzy partitions. Journal
 * of Cybernetics, 4:95–104, 1974.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class DunnIndex<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final long serialVersionUID = -6973489229802690101L;
    private static final String name = "Dunn index";

    public DunnIndex() {
        dm = EuclideanDistance.getInstance();
    }

    public DunnIndex(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        int k = clusters.size();
        if (k < 2) {
            //doesn't make much sense to compute index for one cluster
            return Double.NaN;
        }

        double maxIntraClusterdist = Double.MIN_VALUE, temp;
        double minClusterDistance = Double.MAX_VALUE;
        C clusterX, clusterY;
        ClusterLinkage<E> link = (ClusterLinkage<E>) LinkageFactory.getInstance().getProvider("Single");
        link.setDistanceMeasure(dm);

        for (int i = 0; i < clusters.size();
             i++) {
            clusterX = clusters.get(i);
            //find maximal distance in between each cluster
            temp = maxIntraClusterDistance(clusterX);
            if (temp > maxIntraClusterdist) {
                maxIntraClusterdist = temp;
            }

            for (int j = i + 1; j < clusters.size(); j++) {
                clusterY = clusters.get(j);
                /*
                 * finding minimal distance between objects in both clusters
                 * corresponds to single linkage distance
                 */
                temp = link.distance(clusterX, clusterY);
                if (temp < minClusterDistance) {
                    minClusterDistance = temp;
                }
            }

        }
        return minClusterDistance / maxIntraClusterdist;
    }

    public double maxIntraClusterDistance(C cluster) {
        double max = Double.MIN_VALUE;
        Instance x, y;
        double dist;
        for (int i = 0; i < cluster.size(); i++) {
            x = cluster.instance(i);
            for (int j = i + 1; j < cluster.size(); j++) {
                y = cluster.instance(j);
                dist = dm.measure(x, y);
                if (dist > max) {
                    max = dist;
                }
            }
        }
        return max;
    }

    /**
     * Should be maximized
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        return (score1 > score2);
    }

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
        return Double.POSITIVE_INFINITY;
    }
}
