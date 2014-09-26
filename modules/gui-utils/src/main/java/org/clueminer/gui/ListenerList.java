package org.clueminer.gui;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A swing implementation of listeners list does not consider ordering of
 * listeners. This is a proof of concept of "better listeners" mechanism.
 *
 * @author Tomas Barton
 * @param <T> listener type
 */
public class ListenerList<T> implements Iterable<T> {

    private T[] data;
    private final Map<T, T[]> map;
    private boolean hasConstraints = false;

    /**
     * A list of listeners with consistent order
     */
    public ListenerList() {
        map = new LinkedHashMap<>();
    }

    public ListenerList(int capacity) {
        map = new LinkedHashMap<>(capacity);
    }

    public int size() {
        return map.size();
    }

    public final void ensureCapacity(int requested) {
        if ((requested + 1) > getCapacity()) {
            int capacity = (int) (requested * 1.618); //golden ratio :)
            if (capacity <= size()) {
                capacity = size() * 3; // for small numbers due to int rounding we wouldn't increase the size
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

    /**
     * Adds listener to list
     *
     * @param listner class to be notified
     */
    public void add(T listner) {
        map.put(listner, null);
        clearCache();
    }

    /**
     * Adds listener to list with specified requirements
     *
     * @param listner  class to be notified
     * @param requires which classes should be notified earlier
     */
    public void add(T listner, T[] requires) {
        map.put(listner, requires);
        hasConstraints = true;
        clearCache();
    }

    /**
     * Removes listener from list
     *
     * @param listener
     */
    public void remove(T listener) {
        map.remove(listener);
        clearCache();
    }

    /**
     * Clears created DAG
     */
    private void clearCache() {
        data = null;
    }

    /**
     * Create ordering of listeners which respects constraints
     */
    protected void build() {
        int n = size();
        data = (T[]) new Object[n];
        int i = 0;
        if (!hasConstraints) {
            //most trivial case (no constraints at all)
            for (T obj : map.keySet()) {
                data[i++] = obj;
            }
        }
    }

    protected T get(int index) {
        if (data == null) {
            build();
        }
        return data[index];
    }

    public T[] getListeners() {
        if (data == null) {
            build();
        }
        return data;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Iterator<T> iterator() {
        if (data == null) {
            build();
        }
        return new ListenerIterator();
    }

    class ListenerIterator implements Iterator<T> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public T next() {
            return get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from dataset using the iterator.");

        }
    }

}
