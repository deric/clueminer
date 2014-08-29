package org.clueminer.interpolation;

/**
 * this class represents a cubic polynomial
 *
 * @see http://www.cse.unsw.edu.au/~lambert/splines/
 * @author Tim Lambert
 */
public class Cubic {

    double a, b, c, d;         /* a + b*u + c*u^2 +d*u^3 */


    public Cubic(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    /**
     * evaluate cubic
     *
     * @param u
     * @return
     */
    public double eval(double u) {
        return (((d * u) + c) * u + b) * u + a;
    }
}
