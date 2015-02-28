/*
 * Copyright (C) 2015 clueminer.org
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

import java.util.List;

/**
 * Transforms <tt>List<Type></tt> into <tt>List<AnotherType></tt>
 *
 * @author deric
 * @param <E> original collection
 * @param <F> transformed collection
 */
public abstract class CollectionTransformer<E, F> {

    public abstract F transform(E e);

    /**
     *
     * @param list
     * @param trasformed
     * @return
     */
    public List<F> transform(List<E> list, List<F> trasformed) {
        for (E e : list) {
            trasformed.add(transform(e));
        }
        return trasformed;
    }
}
