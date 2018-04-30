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

import org.apache.commons.math3.util.FastMath;
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
 *
 * @cite Mitligan, G. W. (1981a). A Monte Carlo study of thirty internal
 * criterion measures for cluster analysis. Psychometrika, 46, 187-199.
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = InternalEvaluator.class)
public class PointBiserialNorm<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static String NAME = "PointBiserial-Norm";
    private static final long serialVersionUID = -3222061698654228829L;

    public PointBiserialNorm() {
        dm = EuclideanDistance.getInstance();
    }

    public PointBiserialNorm(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Normalized version by stddev - not necessary when comparing clusterings
     * on the same dataset
     *
     * @param clusters
     * @param params
     * @return
     */
    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double sw = 0, nw = 0;
        double sb = 0, nb = 0;
        double nt, sd, pb;

        C first, second;
        E x, y;
        for (int i = 0; i < clusters.size(); i++) {
            first = clusters.get(i);
            for (int j = 0; j < first.size(); j++) {
                x = first.instance(j);
                // calculate sum of intra cluster distances dw and count their
                // number.
                for (int k = j + 1; k < first.size(); k++) {
                    y = first.instance(k);
                    sw += dm.measure(x, y);
                    nw++;
                }
                // calculate sum of inter cluster distances dw and count their
                // number.
                for (int k = i + 1; k < clusters.size(); k++) {
                    second = clusters.get(k);
                    for (int l = 0; l < second.size(); l++) {
                        y = second.instance(l);
                        sb += dm.measure(x, y);
                        nb++;
                    }
                }
            }
        }
        // calculate total number of distances
        nt = nw + nb;
        // calculate mean dw and db
        double meanSw = sw / nw;
        double meanSb = sb / nb;
        // calculate standard deviation of all distances (sum inter and intra)
        double tmpSdw = 0, tmpSdb = 0;
        double distance;
        for (int i = 0; i < clusters.size(); i++) {
            first = clusters.get(i);
            for (int j = 0; j < first.size(); j++) {
                x = first.instance(j);
                for (int k = j + 1; k < first.size(); k++) {
                    y = first.instance(k);
                    distance = dm.measure(x, y);
                    tmpSdw += FastMath.pow(distance - meanSw, 2);
                }
                for (int k = i + 1; k < clusters.size(); k++) {
                    second = clusters.get(k);
                    for (int l = 0; l < second.size(); l++) {
                        y = second.instance(l);
                        distance = dm.measure(x, y);
                        tmpSdb += FastMath.pow(distance - meanSb, 2);
                    }
                }
            }
        }
        //standard deviation of all distances
        sd = Math.sqrt((tmpSdw + tmpSdb) / nt);
        // calculate point biserial score
        pb = (meanSb - meanSw) * Math.sqrt(((nw * nb) / (nt * nt))) / sd;
        return pb;
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        // should be maximized
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
