package org.clueminer.gui;

/**
 *
 * @author Tomas Barton
 * @param <T> listener type
 */
public class ListenerList<T> {

    private T[] data;
    private int n = 0;

    public int size() {
        return n;
    }

    public final void ensureCapacity(int requested) {
        if ((requested + 1) > getCapacity()) {
            int capacity = (int) (requested * 1.618); //golden ratio :)
            if (capacity <= size()) {
                capacity = n * 3; // for small numbers due to int rounding we wouldn't increase the size
            }
            if (capacity > data.length) {
                T[] tmp = (T[]) new Object[capacity];
                System.arraycopy(data, 0, tmp, 0, data.length);
                data = tmp;
            }
        }
    }

    public int getCapacity() {
        return data.length;
    }

}
