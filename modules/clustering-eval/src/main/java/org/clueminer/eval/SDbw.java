/*
 * Copyright (C) 2011-2018 clueminer.org
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
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * SDbw index
 *
 * @author deric
 * @param <E>
 * @param <C>
 *
 * @cite Halkidi, Maria, and Michalis Vazirgiannis. "Clustering validity
 * assessment: Finding the optimal partitioning of a data set." Data Mining,
 * 2001. ICDM 2001, Proceedings IEEE International Conference on. IEEE, 2001.
 */
@ServiceProvider(service = InternalEvaluator.class)
public class SDbw<E extends Instance, C extends Cluster<E>> extends SDindex<E, C> implements InternalEvaluator<E, C>, ClusterEvaluation<E, C> {

    private static final String NAME = "S_Dbw";
    private static final long serialVersionUID = 2687565191321472835L;

    @Override
    public String getName() {
        return NAME;
    }

    public SDbw() {
        super();
    }

    public SDbw(Distance dist) {
        this.dm = dist;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double density = 0.0;
        double sigma = Math.sqrt(varianceSum(clusters)) / clusters.size();

        for (int i = 0; i < clusters.size(); i++) {
            for (int j = 0; j < i; j++) {
                density += rkk(clusters, sigma, i, j);
            }
        }

        density *= 2.0 / (clusters.size() * (clusters.size() - 1));
        if (Double.isInfinite(density)) {
            return scattering(clusters);
        }

        return scattering(clusters) + density;
    }

    private double rkk(Clustering<E, C> clusters, double sigma, int i, int j) {
        Cluster<E> x = clusters.get(i);
        Cluster<E> y = clusters.get(j);
        //centroid of two clusters
        Instance h = centroid(x, y);

        double denom = Math.max(gamma(sigma, x, y, x.getCentroid()), gamma(sigma, x, y, y.getCentroid()));
        double res = gamma(sigma, x, y, h) / denom;
        return res;
    }

    private E centroid(Cluster<E> x, Cluster<E> y) {
        int attrCount = x.attributeCount();
        if (attrCount == 0) {
            throw new RuntimeException("number of attributes should not be 0");
        }
        E avg = x.builder().build(attrCount);
        for (int j = 0; j < attrCount; j++) {
            avg.set(j, (x.getCentroid().get(j) + y.getCentroid().get(j)) / 2.0);
        }
        return avg;
    }

    private double gamma(double sigma, Cluster x, Cluster y, Instance center) {
        double density = 0;
        double dist;
        for (int k = 0; k < x.size(); k++) {
            dist = dm.measure(x.get(k), center);
            if (dist < sigma) {
                density++;
            }
        }
        for (int k = 0; k < y.size(); k++) {
            dist = dm.measure(y.get(k), center);
            if (dist < sigma) {
                density++;
            }
        }

        return density;
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
