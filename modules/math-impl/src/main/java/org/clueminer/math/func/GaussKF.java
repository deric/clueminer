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
package org.clueminer.math.func;

import static java.lang.Math.PI;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import org.clueminer.math.KernelFunction;

/**
 *
 * @author deric
 */
public class GaussKF implements KernelFunction {

    private static final long serialVersionUID = -6765390012694573184L;

    private GaussKF() {
    }

    private static class SingletonHolder {

        public static final GaussKF INSTANCE = new GaussKF();
    }

    /**
     * Returns the singleton instance of this class
     *
     * @return the instance of this class
     */
    public static GaussKF getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public double k(double u) {
        return Normal.pdf(u, 0, 1);
    }

    @Override
    public double intK(double u) {
        return Normal.cdf(u, 0, 1);
    }

    @Override
    public double k2() {
        return 1;
    }

    @Override
    public double cutOff() {
        /*
         * This is not techincaly correct, as this value of k(u) is still 7.998827757006813E-38
         * However, this is very close to zero, and is so small that k(u)+x = x, for most values of x.
         * Unless this probability si going to be near zero, values past this point will have
         * no effect on the result
         */
        return 13;
    }

    @Override
    public double kPrime(double u) {
        return -exp(-pow(u, 2) / 2) * u / sqrt(2 * PI);
    }

}
