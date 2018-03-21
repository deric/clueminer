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
package org.clueminer.knn;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 * A distance matrix implemented using HashMap
 *
 * @author Tomas Barton
 * @param <T>
 */
public class KnnCache<T> {

    private static KnnCache instance;

    private final Table<Dataset, Integer, T[]> cache;

    private KnnCache() {
        cache = HashBasedTable.create();
    }

    public static KnnCache getInstance() {
        if (instance == null) {
            instance = new KnnCache();
        }
        return instance;
    }

    public boolean containsKey(Dataset<? extends Instance> dataset) {
        return cache.containsRow(dataset);
    }

    public boolean contains(Dataset<? extends Instance> dataset, int id) {
        return cache.contains(dataset, id);
    }

    public T[] get(Dataset<? extends Instance> dataset, int id) {
        return cache.get(dataset, id);
    }

    /**
     * Store k-nn result to cache
     *
     * @param dataset
     * @param id
     * @param inst
     */
    public void put(Dataset<? extends Instance> dataset, int id, T[] inst) {
        cache.put(dataset, id, inst);
    }
}
