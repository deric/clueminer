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
package org.clueminer.distance;

import org.clueminer.distance.api.Distance;
import org.clueminer.distance.api.SymmetricDistance;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * CorrelationDistance[u,v] is equivalent to
 * 1-(u-Mean[u]).(v-Mean[v])/(Norm[u-Mean[u]]Norm[v-Mean[v]]).
 *
 * @see http://reference.wolfram.com/language/ref/CorrelationDistance.html
 * @author Tomas Barton
 */
@ServiceProvider(service = Distance.class)
public class CorrelationDistance extends SymmetricDistance implements Distance {

    private static final String NAME = "Correlation";
    private static final long serialVersionUID = -564035509212244896L;

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Correlation distance is equal to CosineDistance(x - mean[x], y - mean[y])
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        checkInput(x, y);
        double sumX = 0.0, sumY = 0.0, meanX, meanY;

        for (int i = 0; i < x.size(); i++) {
            sumX += x.get(i);
            sumY += y.get(i);
        }
        meanX = sumX / x.size();
        meanY = sumY / y.size();

        Vector<Double> u = x.minus(meanX);
        Vector<Double> v = y.minus(meanY);

        //rest is same as Cosine distance
        double denom = u.pNorm(2) * v.pNorm(2);
        if (denom == 0) {
            return 0.0;
        }
        return 1 - u.dot(v) / denom;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSubadditive() {
        return true;
    }

    @Override
    public boolean isIndiscernible() {
        return false;
    }

    @Override
    public double measure(double[] x, double[] y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
