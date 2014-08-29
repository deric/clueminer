package com.panayotis.gnuplot.dataset;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * This object represents a N-dimensional point.
 * It is used in PointDataSet as the actual data object
 * @param N the type of this object. It should be a Number
 * @author teras
 */
public class Point<N extends Number> implements Serializable {
    private N[] coords;
    
    /** Creates a new instance of Point with given coordinates
     * @param coords The coordinates given as a list of native (or boxed) type numbers.
     */
    @SuppressWarnings("unchecked")
    public Point(N... coords) {
        this.coords = (N[])Array.newInstance(Number.class, coords.length);
        System.arraycopy(coords, 0, this.coords, 0, coords.length);
    }
    
    /**
     * Retrieve the value of a specific coordinate of this point
     * @param dimension the coordination dimension
     * @return the value of this point
     * @throws java.lang.ArrayIndexOutOfBoundsException The coordination required is not
     * present
     */
    public N get(int dimension) throws ArrayIndexOutOfBoundsException {
        return coords[dimension];
    }
    
    /**
     * Retrieve the actual coordinations of this point
     * @return The coordinations (dimensions) of this point
     */
    public int getDimensions() {
        return coords.length;
    }
}
