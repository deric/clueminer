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
 *
 * @author deric
 */
@ServiceProvider(service = Distance.class)
public class HammingDistance extends SymmetricDistance implements Distance {

    private static final long serialVersionUID = -2968578452009496222L;
    private static final String NAME = "Hamming";
    private static HammingDistance instance;

    public static HammingDistance getInstance() {
        if (instance == null) {
            instance = new HammingDistance();
        }
        return instance;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        double dist = 0;
        for (int i = 0; i < x.size(); ++i) {
            dist += Math.abs(x.get(i) - y.get(i));
        }
        return dist;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        double dist = 0;
        double diff;
        for (int i = 0; i < x.size(); ++i) {
            diff = (weights[i] * x.get(i) - weights[i] * y.get(i));
            dist += Math.abs(diff);
        }
        return dist;
    }

    @Override
    public boolean isSubadditive() {
        return true;
    }

    @Override
    public boolean isIndiscernible() {
        return true;
    }

    @Override
    public double measure(double[] x, double[] y) {
        double dist = 0;
        for (int i = 0; i < x.length; ++i) {
            dist += Math.abs(x[i] - y[i]);
        }
        return dist;
    }

}
