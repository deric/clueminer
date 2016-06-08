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

import java.util.HashMap;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Davies-Bouldin index the value of the DB index between [0, infinity) zero
 * being a sign for a good cluster
 *
 * @param <E>
 * @param <C>
 * @cite Davies, David L., and Donald W. Bouldin. "A cluster separation
 * measure." Pattern Analysis and Machine Intelligence, IEEE Transactions on 2
 * (1979): 224-227.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class DaviesBouldin<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final long serialVersionUID = -6973489229802690101L;
    private static final String name = "Davies-Bouldin";

    public DaviesBouldin() {
        dm = EuclideanDistance.getInstance();
    }

    public DaviesBouldin(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double db = 0;
        C x, y;
        double intraX, intraY, max, interGroup, dij;
        E centroidX, centroidY;
        HashMap<Integer, Double> intraDists = new HashMap<>();
        for (int i = 0; i < clusters.size(); i++) {
            x = clusters.get(i);
            centroidX = x.getCentroid();
            max = Double.MIN_VALUE;

            intraX = getClusterIntraDistance(i, x, intraDists);
            // find c(i) and c(j) which maximizes dij
            for (int j = 0; j < clusters.size(); j++) {
                if (i != j) {
                    y = clusters.get(j);
                    centroidY = y.getCentroid();
                    intraY = getClusterIntraDistance(j, y, intraDists);
                    interGroup = dm.measure(centroidX, centroidY);
                    dij = (intraX + intraY) / interGroup;
                    if (dij > max) {
                        max = dij;
                    }
                }
            }
            db += max;
        }

        return db / clusters.size();
    }

    private double getClusterIntraDistance(int i, Cluster<E> x, HashMap<Integer, Double> intraDists) {
        if (!intraDists.containsKey(i)) {
            double val = intraDistance(x);
            intraDists.put(i, val);
            return val;
        }
        return intraDists.get(i);
    }

    /**
     * Compute sum of inner cluster distances to centroid
     *
     * @param cluster
     * @return
     */
    private double intraDistance(Cluster<E> cluster) {
        double intraDist = 0.0;
        E centroid = cluster.getCentroid();
        for (E elem : cluster) {
            intraDist += dm.measure(elem, centroid);
        }
        return intraDist / cluster.size();
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
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getMax() {
        return 0;
    }
}
