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
package org.clueminer.clustering;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.std.DataScaler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple storage for standardized data, stores references to data, not deep
 * copies of the data.
 *
 * @author Tomas Barton
 * @param <E>
 */
public class StdStorage<E extends Instance> {

    private final Dataset<E> dataset;
    private final Table<String, Boolean, Dataset<E>> cache;
    private DataScaler ds;
    private static final Logger LOG = LoggerFactory.getLogger(StdStorage.class);

    /**
     * Currently it is storage only for one dataset
     *
     * @param dataset
     */
    public StdStorage(Dataset<E> dataset) {
        this.dataset = dataset;
        cache = newTable();
    }

    public Dataset<E> get(String method, boolean logscale) {
        if (!isCached(method, logscale)) {
            if (ds == null) {
                ds = new DataScaler();
            }
            LOG.debug("computing normalization {}, using log? {}", method, logscale);
            Dataset<E> norm = ds.standartize(dataset, method, logscale);
            cache.put(method, logscale, norm);
        } else {
            LOG.debug("using cached data for {}, std: {}, using log? {}", dataset.getName(), method, logscale);
        }
        return cache.get(method, logscale);
    }

    public boolean isCached(String method, boolean logscale) {
        return cache.contains(method, logscale);
    }

    /**
     * Garbage collect unused standardizations
     *
     * @param used - list of final clusterings, which are going to be used (one
     *             clustering from each standardization would be enough)
     */
    public void gc(Dataset<? extends Instance>[] used) {
        //TODO: implement
    }

    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    public Table<String, Boolean, Dataset<E>> newTable() {
        return Tables.newCustomTable(
                Maps.<String, Map<Boolean, Dataset<E>>>newHashMap(),
                new Supplier<Map<Boolean, Dataset<E>>>() {
            @Override
            public Map<Boolean, Dataset<E>> get() {
                return Maps.newHashMap();
            }
        });
    }
}
