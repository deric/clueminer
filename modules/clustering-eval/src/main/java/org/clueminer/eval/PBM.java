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

import org.apache.commons.math3.util.FastMath;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = InternalEvaluator.class)
public class PBM extends AbstractEvaluator {

    private static final String name = "PBM";
    private static final long serialVersionUID = -8947980448201668614L;

    public PBM() {
        dm = EuclideanDistance.getInstance();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        double db = Double.MIN_VALUE;
        double tmp, score;
        Instance g = clusters.getCentroid();
        double et = 0.0;
        double ew = 0.0;
        Instance x, y;
        Cluster clust;
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            x = clust.getCentroid();

            for (int j = 0; j < i; j++) {
                y = clusters.get(j).getCentroid();
                tmp = dm.measure(x, y);
                if (tmp > db) {
                    db = tmp;
                }
            }

            //distance of each cluster member to its centroid
            for (int j = 0; j < clust.size(); j++) {
                y = clust.get(j);
                ew += dm.measure(x, y);
                //distance to global centroid
                et += dm.measure(x, g);
            }
        }
        score = (et * db) / (ew * clusters.size());
        return FastMath.pow(score, 2);
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
