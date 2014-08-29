package org.clueminer.interpolation;

import org.clueminer.math.NumericBox;

/**
 *
 * @author Tomas Barton
 */
public class NumberBox implements NumericBox {

    private final Number[] data;

    public NumberBox(Number[] data) {
        this.data = data;
    }

    @Override
    public double get(int index) {
        return data[index].doubleValue();
    }

    @Override
    public int size() {
        return data.length;
    }

}
