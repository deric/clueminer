package org.clueminer.distance.api;

import java.io.Serializable;
import org.clueminer.math.Vector;

/**
 *
 * @author Tomas Barton
 */
public interface Distance extends Serializable {

    String getName();

    /**
     * Calculates the distance between two instances.
     *
     * @param x the first instance
     * @param y the second instance
     * @return the distance between the two instances
     */
    double measure(Vector<Double> x, Vector<Double> y);

    /**
     * Calculates the distance between two instances. For some distance
     * functions weighting does not make sense, in that case method will throw
     * an exception
     *
     * @param x first instance
     * @param y second instance
     * @param weights multiplication factor (usually between 0 and 1)
     * @return
     */
    double measure(Vector<Double> x, Vector<Double> y, double[] weights);

    /**
     * Returns whether the first distance, similarity or correlation is better
     * than the second distance (similarity, correlation, etc.)
     *
     * Both values should be calculated using the same measure.
     *
     * For similarity measures the higher the similarity the better the measure,
     * for distance measures it is the lower the better and for correlation
     * measure the absolute value must be higher.
     *
     * @param x - the first distance, similarity or correlation
     * @param y - the second distance, similarity or correlation
     * @return true if the first distance is better than the second, false in
     * other cases.
     */
    boolean compare(double x, double y);

    double measure(double[] x, double[] y);

    /**
     * Returns the value that this distance metric produces for the lowest
     * distance or highest similarity. This is mainly useful to initialize
     * variables to be used in comparisons with the compare method of this
     * class.
     *
     *
     *
     * @return minimum possible value of the distance metric, if is possible to
     * determine it (otherwise Double.NaN)
     */
    double getMinValue();

    /**
     * Returns the value that this distance metric produces for the highest
     * distance or lowest similarity. This is mainly useful to initialize
     * variables to be used in comparisons with the compare method of this
     * class.
     *
     * @return maximum possible value of the distance metric
     */
    double getMaxValue();

    /**
     * Returns true if {@code d(x,y) == d(y,x)} is true for any {@code x},
     * {@code y}.
     *
     * d(x, y) = d(y, x)
     *
     * @return true if this distance metric is symmetric, false if it is not
     */
    boolean isSymmetric();

    /**
     * Returns true if this distance metric obeys the rule that, for any x, y,
     * and z &isin; S <br>
     * d(x, z) &le; d(x, y) + d(y, z)
     *
     * d(x, z) ≤ d(x, y) + d(y, z) - triangle inequality
     *
     * @return true if this distance metric supports the triangle inequality,
     * false if it does not.
     */
    boolean isSubadditive();

    /**
     * Returns true if this distance metric obeys the rule that, for any x and y
     * &isin; S <br>
     * d(x, y) = 0 if and only if x = y
     *
     * @return true if this distance metric is indiscernible, false otherwise.
     */
    boolean isIndiscernible();
}
