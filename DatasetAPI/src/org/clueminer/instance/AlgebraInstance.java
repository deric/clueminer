package org.clueminer.instance;

/**
 *
 * @author Tomas Barton
 */
public interface AlgebraInstance {

    /**
     * Subtract an instance from this instance and returns the results.
     *
     * This method does not modify this instance, but returns the result.
     *
     * @return result of the subtraction
     */
    public Instance minus(Instance min);

    /**
     * Subtract a scalar from this instance and returns the results.
     *
     * This method does not modify this instance, but returns the result.
     *
     * @return result of the subtraction
     */
    public Instance minus(double value);

    /**
     * Add an instance to this instance and returns the results.
     *
     * This method does not modify this instance, but returns the result.
     *
     * @return result of the addition
     */
    public Instance add(Instance max);

    /**
     * Divide each value of this instance by a scalar value and returns the
     * results.
     *
     * This method does not modify this instance, but returns the result.
     *
     * @return result of the division
     */
    public Instance divide(double value);

    /**
     * Divide each value in this instance with the corresponding value of the
     * other instance and returns the results.
     *
     * This method does not modify this instance, but returns the result.
     *
     * @return result of the division
     */
    public Instance divide(Instance currentRange);

    /**
     * Add a scalar value to this instance and returns the results.
     *
     * This method does not modify this instance, but returns the result.
     *
     * @param value - value to add
     * @return result of the addition
     */
    public Instance add(double value);

    /**
     * Multiply each value of this instance with a scalar value and return the
     * result.
     *
     * @param value - scalar to multiply with
     * @return result of multiplication
     */
    public Instance multiply(double value);

    /**
     * Multiply each value in this instance with the corresponding value in
     * provide instance.
     *
     * @param value instance to multiply with
     * @return result of multiplication.
     */
    public Instance multiply(Instance value);

    /**
     * Take square root of all attributes.
     *
     * @return square root of attribute values
     */
    public Instance sqrt();
}
