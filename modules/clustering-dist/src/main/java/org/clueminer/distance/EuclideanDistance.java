package org.clueminer.distance;

import org.apache.commons.math3.util.FastMath;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Vector;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Distance.class)
public class EuclideanDistance extends MinkowskiDistance {

    private static final String NAME = "Euclidean";
    private static final long serialVersionUID = 3142545695613722167L;
    private static EuclideanDistance instance;
    //whether compute SQRT(sum) or not
    private boolean sqrt = true;

    public EuclideanDistance() {
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
        if (sqrt) {
            return Math.sqrt(sum);
        }
        return sum;
    }

    public double sqdist(Vector<Double> x, Vector<Double> y) {
        checkInput(x, y);
        double sum = 0;
        for (int i = 0; i < x.size(); i++) {
            //should be faster
            sum += FastMath.pow(y.get(i) - x.get(i), power);
        }
        return sum;
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        checkInput(x, y);
        double sum = 0;
        for (int i = 0; i < x.size(); i++) {
            sum += FastMath.pow(weights[i] * y.get(i) - weights[i] * x.get(i), power);
        }

        if (sqrt) {
            return Math.sqrt(sum);
        }
        return sum;
    }

    @Override
    public double measure(double[] x, double[] y) {
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += FastMath.pow(y[i] - x[i], power);
        }

        if (sqrt) {
            return Math.sqrt(sum);
        }
        return sum;
    }

    public boolean isSqrt() {
        return sqrt;
    }

    public void setSqrt(boolean sqrt) {
        this.sqrt = sqrt;
    }

}
