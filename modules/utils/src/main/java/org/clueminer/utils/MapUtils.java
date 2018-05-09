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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Code snippets for operating with Maps
 *
 * @author Tomas Barton
 * @see http://www.techiedelight.com/sort-map-by-values-java/
 * @see https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values
 */
public class MapUtils {

    /**
     * Sort map by value
     *
     * @param <K>
     * @param <V>
     * @param map
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * A Java 8 version using streams
     *
     * @param <K>
     * @param <V>
     * @param map
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue8(Map<K, V> map) {
        return map.entrySet().stream()
                .sorted(Entry.comparingByValue())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

}
