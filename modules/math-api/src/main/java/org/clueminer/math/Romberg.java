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
package org.clueminer.math;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

/**
 *
 * @author deric
 */
public class Romberg {

    public static double romb(Function f, double a, double b) {
        return romb(f, a, b, 20);
    }

    public static double romb(Function f, double a, double b, int max) {
        // see http://en.wikipedia.org/wiki/Romberg's_method

        max += 1;
        double[] s = new double[max];//first index will not be used
        double var = 0;//var is used to hold the value R(n-1,m-1), from the previous row so that 2 arrays are not needed
        double lastVal = Double.NEGATIVE_INFINITY;

        for (int k = 1; k < max; k++) {
            for (int i = 1; i <= k; i++) {
                if (i == 1) {
                    var = s[i];
                    s[i] = Trapezoidal.trapz(f, a, b, (int) pow(2, k - 1));
                } else {
                    s[k] = (pow(4, i - 1) * s[i - 1] - var) / (pow(4, i - 1) - 1);
                    var = s[i];
                    s[i] = s[k];
                }
            }

            if (abs(lastVal - s[k]) < 1e-15)//there is only approximatly 15.955 accurate decimal digits in a double, this is as close as we will get
            {
                return s[k];
            } else {
                lastVal = s[k];
            }
        }

        return s[max - 1];
    }
}
