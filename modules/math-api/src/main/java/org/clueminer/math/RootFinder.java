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

import java.io.Serializable;

/**
 * A general contract for the numerical computation of a root of a given function
 *
 * @author deric
 */
public interface RootFinder extends Serializable {

    /**
     * Attempts to numerical compute the root of a given function, such that f(<tt>args</tt>) = 0. Only one variable may be altered at a time
     *
     * @param eps            the accuracy desired for the solution
     * @param maxIterations  the maximum number of steps allowed before forcing a return of the current solution.
     * @param initialGuesses an array containing the initial guess values
     * @param f              the function to find the root of
     * @param pos            the index of the argument that will be allowed to alter in order to find the root. Starts from 0
     * @param args           the values to be passed to the function as arguments
     * @return the value of the variable at the index <tt>pos</tt> that makes the function return 0
     */
    double root(double eps, int maxIterations, double[] initialGuesses, Function f, int pos, double... args);

    /**
     * Attempts to numerical compute the root of a given function, such that f(<tt>args</tt>) = 0. Only one variable may be altered at a time
     *
     * @param eps            the accuracy desired for the solution
     * @param maxIterations  the maximum number of steps allowed before forcing a return of the current solution.
     * @param initialGuesses an array containing the initial guess values
     * @param f              the function to find the root of
     * @param pos            the index of the argument that will be allowed to alter in order to find the root. Starts from 0
     * @param args           the values to be passed to the function as arguments
     * @return the value of the variable at the index <tt>pos</tt> that makes the function return 0
     */
    double root(double eps, int maxIterations, double[] initialGuesses, Function f, int pos, Vector<Double> args);

    /**
     * Different root finding methods require different numbers of initial guesses.
     * Some root finding methods require 2 guesses, each with values of opposite
     * sign so that they bracket the root. Others just need any 2 initial guesses
     * sufficiently close to the root. This method simply returns the number of
     * guesses that are needed.
     *
     * @return the number of initial guesses this root finding method needs
     */
    int guessesNeeded();
}
