/*
 * Copyright (C) 2011-2015 clueminer.org
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
 * A pair of objects with assigned value (score)
 *
 * @author deric
 * @param <T>
 */
public class PairValue<T> extends Pair<T> implements Comparable<PairValue<T>> {

    private double value;

    public PairValue(T A, T B) {
        super(A, B);
    }

    public PairValue(T A, T B, double value) {
        super(A, B);
        this.value = value;
    }

    @Override
    public int compareTo(PairValue<T> o) {
        PairValue<T> e = (PairValue<T>) o;
        if (value > e.value) {
            return -1;
        } else if (value < e.value) {
            return 1;
        }
        return 0;
    }

    public double getValue() {
        return value;
    }

}
