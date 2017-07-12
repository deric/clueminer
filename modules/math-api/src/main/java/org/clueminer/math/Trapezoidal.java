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
package org.clueminer.math;

/**
 *
 * @author deric
 */
public class Trapezoidal {

    static public double trapz(Function f, double a, double b, int N) {
        /*
         *    b               /              N - 1                 \
         *   /                |              =====                 |
         *  |           b - a |f(a) + f(b)   \      /    k (b - a)\|
         *  | f(x) dx = ----- |----------- +  >    f|a + ---------||
         *  |             N   |     2        /      \        N    /|
         * /                  |              =====                 |
         *  a                 \              k = 1                 /
         */
        double sum = 0;
        for (int k = 1; k < N; k++) {
            sum += f.f(a + k * (b - a) / N);
        }

        sum += (f.f(a) + f.f(b)) / 2;

        return (b - a) / N * sum;
    }
}
