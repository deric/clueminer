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

import static java.lang.Math.abs;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;
import org.clueminer.math.Function;
import org.clueminer.math.RootFinder;
import org.clueminer.math.Vector;

/**
 *
 * @author deric
 */
public class RiddersMethod implements RootFinder {

    private static final long serialVersionUID = 8715861384727077982L;

    public static double root(double a, double b, Function f, double... args) {
        return root(1e-15, 1000, a, b, 0, f, args);
    }

    public static double root(double eps, double a, double b, Function f, double... args) {
        return root(eps, 1000, a, b, 0, f, args);
    }

    public static double root(double eps, double a, double b, int pos, Function f, double... args) {
        return root(eps, 1000, a, b, pos, f, args);
    }

    public static double root(double eps, int maxIterations, double x1, double x2, int pos, Function f, double... args) {
        //We assume 1 dimensional function then
        if (args == null || args.length == 0) {
            pos = 0;
            args = new double[1];
        }

        args[pos] = x1;
        double fx1 = f.f(args);
        args[pos] = x2;
        double fx2 = f.f(args);
        double halfEps = eps * 0.5;

        if (fx1 * fx2 >= 0) {
            throw new ArithmeticException("The given interval does not appear to bracket the root");
        }

        double dif = 1;//Measure the change interface values
        while (abs(x1 - x2) > eps && maxIterations-- > 0) {
            double x3 = (x1 + x2) * 0.5;

            args[pos] = x3;
            double fx3 = f.f(args);

            double x4 = x3 + (x3 - x1) * signum(fx1 - fx2) * fx3 / sqrt(fx3 * fx3 - fx1 * fx2);

            args[pos] = x4;
            double fx4 = f.f(args);
            if (fx3 * fx4 < 0) {
                x1 = x3;
                fx1 = fx3;
                x2 = x4;
                fx2 = fx4;
            } else if (fx1 * fx4 < 0) {
                dif = abs(x4 - x2);
                if (dif <= halfEps)//WE are no longer updating, return the value
                {
                    return x4;
                }
                x2 = x4;
                fx2 = fx4;
            } else {
                dif = abs(x4 - x1);
                if (dif <= halfEps)//WE are no longer updating, return the value
                {
                    return x4;
                }
                x1 = x4;
                fx1 = fx4;
            }

        }

        return x2;
    }

    public double root(double eps, int maxIterations, double[] initialGuesses, Function f, int pos, double... args) {
        return root(eps, maxIterations, initialGuesses[0], initialGuesses[1], pos, f, args);
    }

    public double root(double eps, int maxIterations, double[] initialGuesses, Function f, int pos, Vector<Double> args) {
        return root(eps, maxIterations, initialGuesses[0], initialGuesses[1], pos, f, args.toArray());
    }

    public int guessesNeeded() {
        return 2;
    }
}
