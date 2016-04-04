/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.dataset.plugin;

import java.util.Collection;
import java.util.Iterator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author deric
 * @param <E>
 */
public class AttributeCollection<E extends Number> implements Collection<E> {

    private final Dataset<? extends Instance> dataset;
    private final int attr;

    public AttributeCollection(Dataset<? extends Instance> dataset, int index) {
        this.attr = index;
        this.dataset = dataset;
    }

    @Override
    public int size() {
        return dataset.size();
    }

    @Override
    public boolean isEmpty() {
        return dataset.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < dataset.size(); i++) {
            if (o.equals(dataset.get(i, attr))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new AttributeValuesIterator();
    }

    class AttributeValuesIterator implements Iterator<E> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < dataset.size();
        }

        @Override
        public E next() {
            return (E) (Number) dataset.get(index++, attr);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from dataset using the iterator.");

        }
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
