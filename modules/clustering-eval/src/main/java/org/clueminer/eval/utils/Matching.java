/*
 * Copyright (C) 2011-2017 clueminer.org
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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
    //cached inversed index
    private Multimap<String, String> inverse;

    public Matching() {
        this.matching = Maps.newHashMap();
    }

    /**
     *
     * @return size of matched records
     */
    public int size() {
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

    public Collection<String> keySet() {
        return matching.keySet();
    }

    public Set<Entry<String, String>> entrySet() {
        return matching.entrySet();
    }

    public boolean containsKey(String key) {
        return matching.containsKey(key);
    }

    public String put(String klass, String cluster) {
        inverse = null;
        return matching.put(klass, cluster);
    }

    /**
     * Allows searching in map by value, a cluster might be mapped to multiple
     * classes
     *
     * @return
     */
    public Multimap<String, String> inverse() {
        Multimap<String, String> multi = ArrayListMultimap.create();
        for (Map.Entry<String, String> e : matching.entrySet()) {
            multi.put(e.getValue(), e.getKey());
        }
        inverse = multi;
        return multi;
    }

    /**
     *
     * @param cluster
     * @return class(es) mapped to given cluster, null if none
     */
    public String getByCluster(String cluster) {
        if (inverse == null) {
            inverse();
        }
        if (inverse.containsKey(cluster)) {
            Collection res = inverse.get(cluster);
            return res.toString();
        }
        return null;
    }

    @Override
    public String toString() {
        return matching.toString();
    }

}
