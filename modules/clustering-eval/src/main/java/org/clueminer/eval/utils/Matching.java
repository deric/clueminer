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
package org.clueminer.eval.utils;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.HashMap;

/**
 * A wrapper around a HashMap for class -> cluster assignment
 *
 * Class labels must be unique, however a cluster might contain multiple
 * classes, therefore a cluster name might be repeated in matching records.
 *
 * @author deric
 */
public class Matching {

    private final HashMap<String, String> matching;

    public Matching() {
        this.matching = Maps.newHashMap();
    }

    /**
     *
     * @return size of matched records
     */
    public int size() {
        if (matching == null) {
            return 0;
        }
        return matching.size();
    }

    /**
     * For a given class return matching cluster
     *
     * @param klass
     * @return cluster that is mapped to given class label
     */
    public String get(String klass) {
        return matching.get(klass);
    }

    public Collection<String> values() {
        return matching.values();
    }

    public boolean containsKey(String key) {
        return matching.containsKey(key);
    }

    public String put(String klass, String cluster) {
        return matching.put(klass, cluster);
    }

}
