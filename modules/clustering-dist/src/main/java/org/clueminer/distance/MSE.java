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
package org.clueminer.distance;

import org.clueminer.distance.api.Distance;
import org.clueminer.distance.api.SymmetricDistance;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 * Mean squared error
 *
 * @author deric
 */
@ServiceProvider(service = Distance.class)
public class MSE extends SymmetricDistance {

    private static final String NAME = "MSE";
    private static final long serialVersionUID = -7402972399401757130L;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        double sum = 0.0;
        checkInput(x, y);
        for (int i = 0; i < x.size(); i++) {
            sum += x.get(i) - y.get(i);
        }
        return sum / (double) x.size();
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double measure(double[] x, double[] y) {
        double sum = 0.0;
        checkInput(x, y);
        for (int i = 0; i < x.length; i++) {
            sum += x[i] - y[i];
        }
        return sum / (double) x.length;
    }

    @Override
    public boolean isSubadditive() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isIndiscernible() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
