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
package org.clueminer.eval.utils;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class ValueComparableMap<K extends Comparable<K>, V> extends TreeMap<K, V> {

    private static final long serialVersionUID = 3185543157057470654L;
    //A map for doing lookups on the keys for comparison so we don't get infinite loops
    private final Map<K, V> valueMap;

    ValueComparableMap(final Ordering<? super V> partialValueOrdering) {
        this(partialValueOrdering, new HashMap<K, V>());
    }

    private ValueComparableMap(Ordering<? super V> partialValueOrdering,
            HashMap<K, V> valueMap) {
        super(partialValueOrdering //Apply the value ordering
                .onResultOf(Functions.forMap(valueMap)) //On the result of getting the value for the key from the map
                .compound(Ordering.natural())); //as well as ensuring that the keys don't get clobbered
        this.valueMap = valueMap;
    }

    @Override
    public V put(K k, V v) {
        if (valueMap.containsKey(k)) {
            //remove the key in the sorted set before adding the key again
            remove(k);
        }
        valueMap.put(k, v); //To get "real" unsorted values for the comparator
        return super.put(k, v); //Put it in value order
    }
}
