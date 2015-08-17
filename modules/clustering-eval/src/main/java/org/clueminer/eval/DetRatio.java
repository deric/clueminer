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
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = InternalEvaluator.class)
public class DetRatio extends AbstractEvaluator implements InternalEvaluator {

    private static String NAME = "DetRatio";
    private static final long serialVersionUID = -6861450793005245212L;

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
    public double score(Clustering<? extends Cluster> clusters, Props params) {
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

        return t.det() / wg.det();
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
