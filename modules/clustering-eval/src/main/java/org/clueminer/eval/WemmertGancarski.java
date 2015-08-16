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
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class WemmertGancarski extends AbstractEvaluator implements InternalEvaluator {

    private static final String name = "WemmertGancarski";
    private static final long serialVersionUID = -1206250158135544566L;

    public WemmertGancarski() {
        dm = EuclideanDistance.getInstance();
    }

    public WemmertGancarski(DistanceMeasure distance) {
        this.dm = distance;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        double score = 0.0;
        Cluster clust;
        Instance c, inst;
        double jk, sr;
        int n = 0;
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            sr = 0.0;
            c = clust.getCentroid();
            for (int j = 0; j < clust.size(); j++) {
                inst = clust.get(i);
                sr += r(clusters, inst, c, i);
            }
            sr /= clust.size();
            jk = Math.max(0, 1.0 - sr);
            score += clust.size() * jk;
            n += clust.size();
        }

        return score / n;
    }

    private double r(Clustering<? extends Cluster> clusters, Instance inst, Instance centroid, int id) {
        Instance c;
        double d;
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < clusters.size(); i++) {
            if (i != id) {
                c = clusters.get(i).getCentroid();
                d = dm.measure(c, inst);
                if (d < min) {
                    min = d;
                }
            }
        }
        double dist = dm.measure(inst, centroid);
        return dist / min;
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
        return 0;
    }

    @Override
    public double getMax() {
        return Double.POSITIVE_INFINITY;
    }

}
