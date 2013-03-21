package org.clueminer.utils;

import java.io.Serializable;

/**
 * A wrapper for containing two objects of different types.
 */
public class Duple<T, U> implements Serializable {

    private static final long serialVersionUID = 1L;
    public final T x;
    public final U y;

    public Duple(T x, U y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Duple)) {
            return false;
        }
        Duple d = (Duple) o;
        return (x == d.x || (x != null && x.equals(d.x)))
                && (y == d.y || (y != null && y.equals(d.y)));
    }

    @Override
    public int hashCode() {
        return ((x == null) ? 0 : x.hashCode()) ^ ((y == null) ? 0 : y.hashCode());
    }

    @Override
    public String toString() {
        return "{" + x + ", " + y + "}";
    }
}
