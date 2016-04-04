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
package org.clueminer.utils;

/**
 * Wrapper for assigning weight to an element that needs to be sorted by double
 * value
 *
 * @author deric
 * @param <T>
 */
public class DoubleElem<T> implements Comparable {

    private final T elem;
    private final double value;

    public DoubleElem(T elem, double value) {
        this.elem = elem;
        this.value = value;
    }

    @Override
    public int compareTo(Object o) {
        DoubleElem<T> e = (DoubleElem) o;
        if (value > e.value) {
            return -1;
        } else if (value < e.value) {
            return 1;
        }
        return 0;
    }

    public T getElem() {
        return elem;
    }

    public double getValue() {
        return value;
    }

}
