/*
 * Copyright (C) 2011-2018 clueminer.org
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

import java.io.Serializable;

/**
 * A wrapper for containing two objects of different types.
 *
 * @param <T>
 * @param <U>
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
