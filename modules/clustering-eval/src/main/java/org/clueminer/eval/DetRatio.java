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
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Very similar to {@link Rubin}
 *
 * TODO: dispersion computation is unstable
 *
 * java.lang.ArrayIndexOutOfBoundsException: 846 at
 * org.clueminer.math.impl.DenseVector.set(DenseVector.java:149) at
 * org.clueminer.math.impl.AbstractDoubleVector.set(AbstractDoubleVector.java:197)
 * at
 * org.clueminer.eval.AbstractEvaluator.totalDispersion(AbstractEvaluator.java:272)
 * at org.clueminer.eval.DetRatio.score(DetRatio.java:76)
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = InternalEvaluator.class)
public class DetRatio<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> implements InternalEvaluator<E, C> {

    private static String NAME = "DetRatio";
    private static final long serialVersionUID = -6861450793005245212L;
    private static final Logger LOG = LoggerFactory.getLogger(DetRatio.class);

    public DetRatio() {
        dm = EuclideanDistance.getInstance();
    }

    public DetRatio(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        //int n = clusters.instancesCount();
        //int k = clusters.size();
        //global centroid
        //Instance gc = clusters.getCentroid();

        /*Matrix bg = new SymmetricMatrixDiag(k);

         Instance x, y;
         Vector dx, dy;

         for (int i = 0; i < k; i++) {
         x = clusters.get(i).getCentroid();
         dx = x.minus(gc).times(x.size());
         for (int j = 0; j <= i; j++) {
         y = clusters.get(j).getCentroid();
         dy = y.minus(gc);
         bg.set(i, j, dx.dot(dy));
         }
         }*/
        Matrix t = totalDispersion(clusters);
        Matrix wg = withinGroupScatter(clusters);

        double ratio = Double.NaN;
        try {
            ratio = t.det() / wg.det();
        } catch (RuntimeException ex) {
            //LU decomposition errors
            LOG.warn(ex.getMessage());
        }

        return ratio;
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
