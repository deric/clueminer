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
 * Simplified silhoulette index
 *
 * @author deric
 */
@ServiceProvider(service = InternalEvaluator.class)
public class SilhouetteSimpl extends Silhouette {

    private static String name = "Silhouette Simpl";
    private static final long serialVersionUID = 2679542818862912390L;

    public SilhouetteSimpl() {
        dm = new EuclideanDistance();
    }

    public SilhouetteSimpl(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        //Silhouette Coefficent is only defined if number of labels
        // is 2 <= num_clusters <= num_samples - 1.
        if (clusters.size() == 1 || clusters.size() >= clusters.instancesCount()) {
            return Double.NaN; // coeficient is not defined for such clusterings
        }
        double score = 0.0;
        //for each cluster
        for (int i = 0; i < clusters.size(); i++) {
            score += clusterScore(clusters.get(i), clusters, i);
        }
        return (score / (double) clusters.size());
    }

    /**
     * Score for single cluster
     *
     * @param clust
     * @param clusters
     * @param i
     * @return
     */
    public double clusterScore(Cluster clust, Clustering clusters, int i) {
        double clusterDist = 0.0;

        //calculate distance to all other objects in cluster
        for (int j = 0; j < clust.size(); j++) {
            clusterDist += instanceScore(clust, clusters, i, clust.instance(j));
        }
        return (clusterDist / (double) clust.size());
    }

    /**
     *
     * @param clust
     * @param clusters
     * @param i        index of cluster
     * @param x
     * @return
     */
    public double instanceScore(Cluster clust, Clustering clusters, int i, Instance x) {
        double b, denom;

        //find minimal distance to other clusters
        b = minDistance(x, clusters, i);
        //SWC1 - distance to a centroid
        denom = dm.measure(x, clust.getCentroid());
        //avoid NaN, if possible
        if (denom == 0.0) {
            return 0.0;
        }
        return b / denom;
    }

    /**
     * Average distance
     *
     * @param clust
     * @param clusters
     * @param i
     * @param x
     * @return
     */
    public double avgDistance(Cluster clust, Clustering clusters, int i, Instance x) {
        Instance y;
        double a, dist;
        a = 0;
        //we can't compute Silhouette for cluster with single item
        if (clust.size() > 1) {
            for (int k = 0; k < clust.size(); k++) {
                y = clust.instance(k);
                if (x.getIndex() != y.getIndex()) {
                    dist = dm.measure(x, y);
                    a += dist;
                }
            }
            //average distance
            a /= clust.size();
        } else {
            //arbitrary defined value, according to the original paper
            a = 0.0;
        }
        return a;
    }

}
