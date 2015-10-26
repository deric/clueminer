package org.clueminer.clustering;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.std.DataScaler;

/**
 * Simple storage for standardized data, stores references to data, not deep
 * copies of the data.
 *
 * @author Tomas Barton
 * @param <E>
 */
public class StdStorage<E extends Instance> {

    private final Dataset<E> dataset;
    private final Table<String, Boolean, Dataset<? extends Instance>> cache;
    private DataScaler ds;

    /**
     * Currently it is storage only for one dataset
     *
     * @param dataset
     */
    public StdStorage(Dataset<E> dataset) {
        this.dataset = dataset;
        cache = newTable();
    }

    public Dataset<? extends Instance> get(String method, boolean logscale) {
        if (!isCached(method, logscale)) {
            if (ds == null) {
                ds = new DataScaler();
            }
            Dataset<E> norm = ds.standartize(dataset, method, logscale);
            cache.put(method, logscale, norm);
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

    public static Table<String, Boolean, Dataset<? extends Instance>> newTable() {
        return Tables.newCustomTable(
                Maps.<String, Map<Boolean, Dataset<? extends Instance>>>newHashMap(),
                new Supplier<Map<Boolean, Dataset<? extends Instance>>>() {
                    @Override
                    public Map<Boolean, Dataset<? extends Instance>> get() {
                        return Maps.newHashMap();
                    }
                });
    }
}
