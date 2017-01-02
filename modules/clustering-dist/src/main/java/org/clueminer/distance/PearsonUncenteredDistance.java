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
package org.clueminer.distance;

import org.clueminer.distance.api.Distance;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Distance.class)
public class PearsonUncenteredDistance extends PearsonDistance {

    private static final long serialVersionUID = 1420036724161464942L;
    private static String name = "Pearson (Uncentered)";

    @Override
    public String getName() {
        return name;
    }

    /**
     * "Uncentered" Pearson correlation d = 1 - r (value lies between 0 and 2)
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        int n, j;
        double xt, yt;
        double sumX2 = 0.0;
        double sumY2 = 0.0;
        double sumXY = 0.0;
        //number of non-zero elements
        n = 0;
        for (j = 0; j < x.size(); j++) {
            if ((!Double.isNaN(x.get(j))) && (!Double.isNaN(y.get(j)))) {
                xt = x.get(j);
                yt = y.get(j);

                sumXY += xt * yt;
                sumX2 += xt * xt;
                sumY2 += yt * yt;
                n++;
            }
        }

        return correlation(n, sumXY, sumX2, sumY2);
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        int n, j;
        double xt, yt;

        double sumX2 = 0.0;
        double sumY2 = 0.0;
        double sumXY = 0.0;
        double w;
        //number of non-zero elements
        n = 0;
        for (j = 0; j < x.size(); j++) {
            if ((!Double.isNaN(x.get(j))) && (!Double.isNaN(y.get(j)))) {
                w = weights[j];
                xt = x.get(j);
                yt = y.get(j);

                sumXY += w * xt * yt;
                sumX2 += w * xt * xt;
                sumY2 += w * yt * yt;
                n++;
            }
        }

        return correlation(n, sumXY, sumX2, sumY2);
    }
}
