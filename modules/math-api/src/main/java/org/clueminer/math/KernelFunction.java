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

import java.io.Serializable;

/**
 * Class for representing one dimensional kernel functions. Since they require
 * no parameters and have no need for duplication, its is advised to make
 * them singletons.
 *
 * See http://en.wikipedia.org/wiki/Kernel_(statistics)
 *
 * @author Edward Raff
 */
public interface KernelFunction extends Serializable {

    /**
     * Returns the weight to be applied to a sample for the normalized distance of two data points.
     *
     * @param u the distance of the data points
     * @return the value in [0, 1) of the amount of weight to give to the sample based on its distance
     */
    double k(double u);

    /**
     * Computes the value of the finite integral from -Infinity up to the value u, of the function given by {@link #k(double) }
     *
     * @param u the distance of the data points
     * @return the value of the integration
     */
    double intK(double u);

    /**
     *
     * Returns the value of the derivative at a point, k'(u)
     *
     * @param u the distance of the data points
     * @return the value of the derivative at <tt>u</tt>
     */
    double kPrime(double u);

    /**
     * Returns the variance of the kernel function
     *
     * @return the variance of the kernel function
     */
    double k2();

    /**
     * As the value of |u| for the kernel function approaches infinity, the
     * value of k(u) approaches zero. This function returns the minimal
     * absolute value of u for which k(u) returns 0
     *
     * @return the first value for which k(u) = 0
     */
    double cutOff();

    /**
     * Returns the name of this kernel function
     *
     * @return the name of this kernel function
     */
    @Override
    String toString();
}
