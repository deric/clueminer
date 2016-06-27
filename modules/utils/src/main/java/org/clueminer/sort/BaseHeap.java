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
package org.clueminer.sort;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import org.openide.util.Exceptions;

/**
 * Helper methods for heap (a binary tree).
 *
 * @author deric
 * @param <T> element type
 */
public abstract class BaseHeap<T> implements Iterable<T> {

    /**
     * The heap array.
     */
    protected T[] heap;

    /**
     *
     * @return capacity of heap
     */
    public abstract int size();

    public abstract T get(int i);

    public void print() {
        try {
            OutputStreamWriter out = new OutputStreamWriter(System.out);
            printTree(out, 0);
            out.flush();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    int left(final int i) {
        return 2 * i + 1;
    }

    int right(final int i) {
        return 2 * i + 2;
    }

    public void printTree(OutputStreamWriter out, int index) throws IOException {
        if (index >= size()) {
            return;
        }
        printTree(out, left(index), false, "");
        printNodeValue(out, index);
        printTree(out, right(index), true, "");
    }

    protected void printNodeValue(OutputStreamWriter out, int index) throws IOException {
        out.write("#" + index + " (" + heap[index] + ")");
        out.write('\n');
    }

    public void printTree(OutputStreamWriter out, int index, boolean isRight, String indent) throws IOException {
        if (index >= size() || index < 0) {
            return;
        }
        printTree(out, left(index), false, indent + (isRight ? " |      " : "        "));

        out.write(indent);
        if (isRight) {
            out.write(" \\");
        } else {
            out.write(" /");
        }
        out.write("----- ");
        printNodeValue(out, index);
        printTree(out, right(index), true, indent + (isRight ? "        " : " |      "));
    }

    protected class HeapIterator<T> implements Iterator<T> {

        private int index;

        public HeapIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public T next() {
            int idx = index++;
            return (T) get(idx);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove using iterator.");
        }
    }

    @Override
    public final Iterator<T> iterator() {
        return new HeapIterator<>();
    }

}
