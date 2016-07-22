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

import java.util.Iterator;
import org.apache.commons.math3.util.FastMath;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.SymmetricMatrix;
import org.clueminer.neighbor.KNNSearch;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.utils.Props;

/**
 * Density-Based Clustering Validation
 *
 * @cite Moulavi, Davoud, Pablo A. Jaskowiak, Ricardo JGB Campello,
 * Arthur Zimek, and Jörg Sander. "Density-Based Clustering Validation."
 * In SDM, pp. 839-847. 2014.
 *
 * @TODO work in progress - not complete implementation
 * @author deric
 * @param <E>
 * @param <C>
 */
public class DBCV<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final String NAME = "DBCV";

    public DBCV() {
        dm = EuclideanDistance.getInstance();
    }

    public DBCV(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) throws ScoreException {
        double score = 0.0;
        //TODO: filter out singleton clusters and noise
        if (clusters.isEmpty()) {
            return score;
        }
        int n = clusters.instancesCount();
        KNNSearch<E> knn = getKnn(clusters, params);

        C cluster;
        Iterator<C> iter = clusters.withoutNoise();
        int k = 0;
        while (iter.hasNext()) {
            cluster = iter.next();
            if (cluster.size() > 1) {
                //knn for each cluster separatedly
                knn.setDataset(cluster);
                Matrix mrg = mutualReachableGraph(cluster, knn);
                mrg.printLower(2, 2);
                score += cluster.size() / (double) n * clusterValidity(clusters, cluster.getClusterId());
                k++;
            }
        }

        return score;
    }

    private Matrix mutualReachableGraph(C cluster, KNNSearch<E> knn) {
        SymmetricMatrix mrg = new SymmetricMatrix(cluster.size());
        E inst;
        double dist, max, core1, core2;
        for (int i = 0; i < cluster.size(); i++) {
            inst = cluster.get(i);
            core1 = coredist(i, cluster, knn);
            for (int j = 0; j < i; j++) {
                dist = dm.measure(inst, cluster.get(j));
                core2 = coredist(j, cluster, knn);
                max = max(dist, core1, core2);
                System.out.println("(" + i + ", " + j + ") = " + max);
                mrg.set(i, j, max);
            }
        }
        return mrg;
    }

    private double coredist(int i, C cluster, KNNSearch<E> nns) {
        int d = cluster.attributeCount();
        double dist = 0.0, core;
        //basicly sort distances in a cluster and skip the closest neighbor
        //TODO: there's no need for knn in here: just skip itself and nearest neighbor
        Neighbor[] nn = nns.knn(cluster.get(i), cluster.size());

        for (int j = 1; j < cluster.size(); j++) {
            dist += FastMath.pow(1.0 / nn[i].distance, d);
        }
        core = Math.pow(dist / (cluster.size() - 1), -1.0 / d);
        if (Double.isInfinite(core)) {
            return 0.0;
        }
        return core;
    }

    private double clusterValidity(Clustering<E, C> clusters, int i) {
        double nom = 0, denom = 0.0;
        double dscp = Double.POSITIVE_INFINITY;
        double dsc;

        //nom = min - sparseness;

        return nom / denom;
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        return score1 > score2;
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public double getMin() {
        return 0.0;
    }

    @Override
    public double getMax() {
        return 1.0;
    }
}
