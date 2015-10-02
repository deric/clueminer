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
import org.clueminer.math.Matrix;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = Distance.class)
public class HammingDistance extends SymmetricDistance implements Distance {

    private static final long serialVersionUID = -2968578452009496222L;
    private static final String name = "Hamming";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getSimilarityFactor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNodeOffset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double rows(Matrix a, Matrix b, int i, int j) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double columns(Matrix a, int i, int j) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        double dist = 0;

        for (int i = 0; i < x.size(); ++i) {
            double diff = (x.get(i) - y.get(i));
            dist += Math.abs(diff);
        }
        return dist;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        double dist = 0;

        for (int i = 0; i < x.size(); ++i) {
            double diff = (weights[i] * x.get(i) - weights[i] * y.get(i));
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

}
