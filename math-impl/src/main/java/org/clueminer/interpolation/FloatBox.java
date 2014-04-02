package org.clueminer.interpolation;

import org.clueminer.math.NumericBox;

/**
 * Immutable data proxy
 *
 * @author Tomas Barton
 */
public class FloatBox implements NumericBox {

    private final float[] data;

    public FloatBox(float[] data) {
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
