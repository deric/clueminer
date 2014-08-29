package org.clueminer.interpolation;

import org.clueminer.math.Numeric;
import org.clueminer.math.NumericBox;

/**
 * Putting things in boxes. There's a box for everything, for double, float,
 * even somewhere in behind there's a box for your mother in law.
 *
 * @author Tomas Barton
 */
public class NumBox implements NumericBox {

    private final Numeric[] data;

    public NumBox(Numeric[] data) {
        this.data = data;
    }

    @Override
    public double get(int index) {
        return data[index].getValue();
    }

    @Override
    public int size() {
        return data.length;
    }

}
