/*
 * Copyright (C) 2011-2016 clueminer.org
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

import org.apache.commons.math3.util.FastMath;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = Distance.class)
public class NegativeEuclidean extends MinkowskiDistance {

    private static final String NAME = "Negative Euclidean";
    private static final long serialVersionUID = 3142545695613722167L;
    private static EuclideanDistance instance;
    //whether compute SQRT(sum) or not
    private boolean squared = true;

    public NegativeEuclidean() {
        this.power = 2;
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Euclidean distance is quite frequently used, there's no need to create
     * instances all over again
     *
     * @return
     */
    public static EuclideanDistance getInstance() {
        if (instance == null) {
            instance = new EuclideanDistance();
        }
        return instance;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        checkInput(x, y);
        double sum = 0;
        for (int i = 0; i < x.size(); i++) {
            //should be faster
            sum += FastMath.pow(y.get(i) - x.get(i), power);
        }
        if (!squared) {
            return Math.sqrt(sum);
        }
        return -sum;
    }

    public double sqdist(Vector<Double> x, Vector<Double> y) {
        checkInput(x, y);
        double sum = 0;
        for (int i = 0; i < x.size(); i++) {
            //should be faster
            sum += FastMath.pow(y.get(i) - x.get(i), power);
        }
        return -sum;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        checkInput(x, y);
        double sum = 0;
        for (int i = 0; i < x.size(); i++) {
            sum += FastMath.pow(weights[i] * y.get(i) - weights[i] * x.get(i), power);
        }

        if (!squared) {
            return Math.sqrt(sum);
        }
        return -sum;
    }

    @Override
    public double measure(double[] x, double[] y) {
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += FastMath.pow(y[i] - x[i], power);
        }

        if (!squared) {
            return Math.sqrt(sum);
        }
        return -sum;
    }

    public boolean isSquared() {
        return squared;
    }

    public void setSquared(boolean squared) {
        this.squared = squared;
    }

}
