package org.clueminer.dataset;

import java.util.*;
import org.openide.util.Lookup;

public class DatasetFactory {

    private static DatasetFactory instance;
    private LinkedHashMap<String, Dataset> datasets;

    public static DatasetFactory getDefault() {
        if (instance == null) {
            instance = new DatasetFactory();
        }
        return instance;
    }

    private DatasetFactory() {
        datasets = new LinkedHashMap<String, Dataset>();
        Collection<? extends Dataset> list = Lookup.getDefault().lookupAll(Dataset.class);
        for (Dataset c : list) {
            datasets.put(c.getName(), c);
        }
        sort();
    }

    private void sort() {
        List<String> mapKeys = new ArrayList<String>(datasets.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, Dataset> someMap = new LinkedHashMap<String, Dataset>();
        for (int i = 0; i < mapKeys.size(); i++) {
            someMap.put(mapKeys.get(i), datasets.get(mapKeys.get(i)));
        }
        datasets = someMap;
    }

    public Dataset getDataset(String key) {
        return datasets.get(key);
    }

    public List<String> getDatasets() {
        List<String> list = new ArrayList<String>(datasets.keySet());
        Collections.sort(list);
        return list;
    }

    public List<String> removeDataset(String current) {
        List<String> list = new ArrayList<String>(datasets.keySet());
        list.remove(current);
        Collections.sort(list);
        return list;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("DatasetFactory [ \n");
        str.append(datasets.toString());
        str.append("\n ]");
        return str.toString();
    }
}
