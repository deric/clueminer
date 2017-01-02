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
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * This index represents an adaptation of Goodman and Kruskal's Gamma statistic
 * for use in a clustering situation (Baker & Hubert, 1975). The index is
 * computed as [ s(+) - s(-) ] / [ s(+) + s(-) ] where s(+) represents the
 * number of consistent comparisons involving between and within cluster
 * distances, and s(-) represents the number of inconsistent outcomes (Milligan,
 * 1981a). Maximum values were taken to represent the correct hierarchy level.
 *
 * @param <E>
 * @param <C>
 * @cite F. B. Baker and L. J. Hubert. Measuring the power of hierarchical
 * cluster analysis. Journal of the American Statistical Association, 70:31–38,
 * 1975
 * @cite Baker, F.; Hubert, L. A graph-theoretic approach to goodness-of-fit in
 * complete-link hierarchical clustering. Journal of the American Statistical
 * Association, 1976: pp. 870– 878.
 *
 * @author Tomas Barton
 *
 */
@ServiceProvider(service = InternalEvaluator.class)
public class Gamma<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final String NAME = "Gamma";
    private static final long serialVersionUID = 4782242459481724512L;
    public static final String S_PLUS = "s+";
    public static final String S_MINUS = "s-";

    public Gamma() {
        dm = EuclideanDistance.getInstance();
    }

    public Gamma(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        Sres s = computeSTable(clusters);
        // calculate gamma
        return (s.plus - s.minus) / (double) (s.plus + s.minus);
    }

    public Sres computeSTable(Clustering<E, C> clusters) {
        Instance x, y;
        Cluster c;
        double distance;
        Sres s = new Sres();

        if (clusters.hasValidation(S_PLUS) && clusters.hasValidation(S_MINUS)) {
            s.plus = (int) clusters.getValidation(S_PLUS);
            s.minus = (int) clusters.getValidation(S_MINUS);
        } else {
            int numWClustPairs = numW(clusters);
            double[] iw = new double[numWClustPairs];
            int l = 0;
            // calculate max intra cluster distance
            for (int i = 0; i < clusters.size(); i++) {
                c = clusters.get(i);
                for (int j = 0; j < c.size() - 1; j++) {
                    x = c.instance(j);
                    for (int k = j + 1; k < c.size(); k++) {
                        y = c.instance(k);
                        distance = dm.measure(x, y);
                        iw[l++] = distance;
                    }
                }
            }
            betweenDistance(clusters, iw, s);
        }
        return s;
    }

    protected void betweenDistance(Clustering<E, C> clusters, double[] withinDist, Sres s) {
        Instance x, y;
        Cluster a, b;
        double distance;
        for (int i = 0; i < clusters.size() - 1; i++) {
            a = clusters.get(i);
            for (int j = 0; j < a.size(); j++) {
                x = a.instance(j);
                for (int k = i + 1; k < clusters.size(); k++) {
                    b = clusters.get(k);
                    for (int l = 0; l < b.size(); l++) {
                        y = b.instance(l);
                        distance = dm.measure(x, y);
                        for (int m = 0; m < withinDist.length; m++) {
                            //we don't count cases when distance is the same
                            if (distance < withinDist[m]) {
                                s.plus++;
                            }
                            if (distance > withinDist[m]) {
                                s.minus++;
                            }
                        }
                    }
                }
            }
        }
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
        // should be maximized. range = [-1,1]
        return score1 > score2;
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public double getMin() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double getMax() {
        return Double.POSITIVE_INFINITY;
    }

    public class Sres {

        /**
         * avoid integer overflow for huge numbers
         */
        public long plus;
        public long minus;

        public Sres() {
            plus = 0;
            minus = 0;
        }
    }

}
