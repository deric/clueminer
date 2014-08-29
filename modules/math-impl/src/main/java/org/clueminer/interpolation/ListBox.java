package org.clueminer.interpolation;

import java.util.List;
import org.clueminer.math.NumericBox;

/**
 *
 * @author Tomas Barton
 */
public class ListBox implements NumericBox {

    private final List data;

    public ListBox(List data) {
        this.data = data;
    }

    @Override
    public double get(int index) {
        return (Double) data.get(index);
    }

    @Override
    public int size() {
        return data.size();
    }

}
