package org.clueminer.factory;

import java.util.*;
import org.clueminer.chart.api.Chart;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class ChartFactory {

    private static ChartFactory instance;
    private LinkedHashMap<String, Chart> charts;

    public static ChartFactory getDefault() {
        if (instance == null) {
            instance = new ChartFactory();
        }
        return instance;
    }

    private ChartFactory() {
        charts = new LinkedHashMap<String, Chart>();
        Collection<? extends Chart> list = Lookup.getDefault().lookupAll(Chart.class);
        for (Chart c : list) {
            charts.put(c.getName(), c);
        }
        sort();
    }

    private void sort() {
        List<String> mapKeys = new ArrayList<String>(charts.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, Chart> someMap = new LinkedHashMap<String, Chart>();
        for (int i = 0; i < mapKeys.size(); i++) {
            someMap.put(mapKeys.get(i), charts.get(mapKeys.get(i)));
        }
        charts = someMap;
    }

    public Chart getChart(String key) {
        return charts.get(key);
    }

    public List<String> getCharts() {
        List<String> list = new ArrayList<String>(charts.keySet());
        Collections.sort(list);
        return list;
    }

    public List<String> removeChart(String current) {
        List<String> list = new ArrayList<String>(charts.keySet());
        list.remove(current);
        Collections.sort(list);
        return list;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("ChartFactory [ \n");
        str.append(charts.toString());
        str.append("\n ]");
        return str.toString();
    }
}
