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
package org.clueminer.chameleon.mo;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Common queue methods.
 *
 * @author deric
 */
public abstract class AbstractQueue<P> implements Queue<P> {

    /**
     * See {@link java.util.Queue#offer}
     *
     * @param e
     * @return
     */
    @Override
    public boolean offer(P e) {
        return add(e);
    }

    /**
     * See {@link java.util.Queue#element}
     *
     * @return first element in the queue (but does not remove it)
     */
    @Override
    public P element() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return peek();
    }

    @Override
    public P remove() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return poll();
    }

    /**
     * See {@link java.util.Queue#peek}
     *
     * @return first element in the queue (but does not remove it)
     */
    @Override
    public P peek() {
        Iterator<P> iter = iterator();
        return iter.next();
    }

    /**
     * See {@link java.util.Collection#addAll}
     *
     * @return true when queue was modified
     */
    @Override
    public boolean addAll(Collection<? extends P> coll) {
        boolean changed = false;
        for (P item : coll) {
            changed |= add(item);
        }
        return changed;
    }

    @Override
    public boolean contains(Object o) {
        Iterator<P> iter = iterator();
        P item;
        while (iter.hasNext()) {
            item = iter.next();
            if (item.equals(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
