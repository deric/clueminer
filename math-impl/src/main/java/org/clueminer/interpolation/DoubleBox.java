package org.clueminer.interpolation;

import org.clueminer.math.NumericBox;

/**
 * Immutable proxy
 *
 * @author Tomas Barton
 */
public class DoubleBox implements NumericBox {

    private final double[] data;

    public DoubleBox(double[] data) {
        this.data = data;
    }

    @Override
    public double get(int index) {
        return data[index];
    }

    @Override
    public int size() {
        return data.length;
    }

}
