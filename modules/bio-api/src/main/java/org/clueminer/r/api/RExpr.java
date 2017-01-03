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
package org.clueminer.r.api;

/**
 * Represent result of R operation
 *
 * @author deric
 */
public interface RExpr {

    double asDouble();

    /**
     * Convert result to an array of Doubles
     *
     * @return
     */
    double[] asDoubles();

    int asInteger();

    /**
     * Convert result to an array of integers
     *
     * @return array of integers
     */
    int[] asIntegers();

    /**
     * Convert result to an array of Strings
     *
     * @return array of strings
     */
    String[] asStrings();

    double[][] asDoubleMatrix();
}
