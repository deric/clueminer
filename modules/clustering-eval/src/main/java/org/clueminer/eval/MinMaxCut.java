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
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = InternalEvaluator.class)
public class MinMaxCut<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final String NAME = "min-max cut";
    private static final long serialVersionUID = -4963722097900153865L;

    public MinMaxCut() {
        dm = EuclideanDistance.getInstance();
    }

    public MinMaxCut(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        Cluster<E> a, b;
        double sum = 0;
        for (int i = 0; i < clusters.size(); i++) {
            double tmpTop = 0;
            double tmp = 0;
            a = clusters.get(i);
            for (int j = 0; j < a.size(); j++) {
                for (int k = 0; k < clusters.size(); k++) {
                    b = clusters.get(k);
                    for (int p = 0; p < b.size(); p++) {
                        if (a.instance(j) != b.instance(p)) {
                            double error = dm.measure(a.instance(j), b.instance(p));
                            tmpTop += error;
                        }
                    }
                }
                for (int k = 0; k < a.size(); k++) {
                    double error = dm.measure(a.instance(j), a.instance(k));
                    tmp += error;
                }
            }
            double tmpSum = tmpTop / tmp;
            sum += tmpSum;
        }
        return sum;
    }

    @Override
    public double score(Clustering clusters, Matrix proximity, Props params) {
        return score(clusters, params);
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        // should be minimized
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
        return Double.NEGATIVE_INFINITY;
    }
}
