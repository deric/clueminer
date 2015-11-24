package org.clueminer.fastcommunity;

import org.clueminer.clustering.aggl.Element;

/**
 *
 * @author Hamster
 */
public class ReverseElement extends Element {

    public ReverseElement(double value, int row, int column) {
        super(value, row, column);
    }

    @Override
    public int compareTo(Object o) {
        Element other = (Element) o;
        double diff = this.getValue() - other.getValue();
        if (diff < 0) {
            return 1;
        } else if (diff > 0) {
            return -1;
        }
        return 0;
    }
}
