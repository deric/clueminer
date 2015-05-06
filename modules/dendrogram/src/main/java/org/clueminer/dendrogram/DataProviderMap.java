package org.clueminer.dendrogram;

import java.util.Map;
import org.clueminer.dataset.api.DataProvider;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class DataProviderMap implements DataProvider {

    private final Map<String, Dataset<? extends Instance>> data;

    public DataProviderMap(Map<String, Dataset<? extends Instance>> data) {
        this.data = data;
    }

    @Override
    public String[] getDatasetNames() {
        return data.keySet().toArray(new String[data.size()]);
    }

    @Override
    public Dataset<? extends Instance> getDataset(String name) {
        return data.get(name);
    }

    @Override
    public Dataset<? extends Instance> first() {
        return data.values().iterator().next();
    }

    @Override
    public int count() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

}
