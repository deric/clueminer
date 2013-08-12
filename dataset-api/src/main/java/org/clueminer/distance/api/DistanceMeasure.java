package org.clueminer.distance.api;

import java.io.Serializable;
import org.clueminer.math.Matrix;
import org.clueminer.math.Vector;

/**
 *
 * @author Tomas Barton
 */
public interface DistanceMeasure extends Serializable {

    public String getName();

    /**
     * Calculates the distance between two instances.
     *
     * @param i the first instance
     * @param j the second instance
     * @return the distance between the two instances
     */
    public double measure(Vector<Double> x, Vector<Double> y);

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
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights);

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
    public boolean compare(double x, double y);

    /**
     * Returns the value that this distance metric produces for the lowest
     * distance or highest similarity. This is mainly useful to initialize
     * variables to be used in comparisons with the compare method of this
     * class.
     *
     *
     *
     * @return minimum possible value of the distance metric
     */
    public double getMinValue();

    /**
     * Returns the value that this distance metric produces for the highest
     * distance or lowest similarity. This is mainly useful to initialize
     * variables to be used in comparisons with the compare method of this
     * class.
     *
     * @return maximum possible value of the distance metric
     */
    public double getMaxValue();
    
    /**
     * @deprecated 
     * @param a
     * @param b
     * @param i
     * @param j
     * @return 
     */
    public double rows(Matrix a, Matrix b, int i, int j);
    
    /**
     * @deprecated
     * @param a
     * @param i
     * @param j
     * @return 
     */
    public double columns(Matrix a, int i, int j);

    /**
     * @deprecated
     * @param matrix
     * @param e1
     * @param e2
     * @param factor
     * @return 
     */
    public double rows(Matrix matrix, int e1, int e2, float factor);
    
    /**
     * @deprecated
     * @param matrix
     * @param e1
     * @param e2
     * @return 
     */
    public double rows(Matrix matrix, int e1, int e2);

    /**
     * @deprecated
     * @param A
     * @param B
     * @param e1
     * @param e2
     * @param factor
     * @return 
     */
    public double rows(Matrix A, Matrix B, int e1, int e2, float factor);
    
    /**
     * @deprecated
     * @param matrix
     * @param e1
     * @param e2
     * @param factor
     * @return 
     */
    public double columns(Matrix matrix, int e1, int e2, float factor);

    public int getNodeOffset();

    public boolean useTreeHeight();

    public float getSimilarityFactor();

    /**
     * Returns true if {@code sim(A,B) == sim(B,A)} is true for any {@code A},
     * {@code B}.
     */
    public boolean isSymmetric();
}
