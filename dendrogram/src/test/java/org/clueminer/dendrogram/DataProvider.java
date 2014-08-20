package org.clueminer.dendrogram;

import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class DataProvider {

    private final Map<String, Dataset<? extends Instance>> data;

    public DataProvider(Map<String, Dataset<? extends Instance>> data) {
        this.data = data;
    }

    public String[] getDatasetNames() {
        return data.keySet().toArray(new String[data.size()]);
    }

    public Dataset<? extends Instance> getDataset(String name) {
        return data.get(name);
    }

    public Dataset<? extends Instance> first() {
        return data.values().iterator().next();
    }

}
