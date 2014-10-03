package org.clueminer.dataset.std;

import java.util.Map.Entry;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.DataStandardization;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.stats.AttrNumStats;
import org.openide.util.lookup.ServiceProvider;

/**
 * Divide each attribute value of a row by maximum value of that attribute. This
 * will put all values to an interval between −1 and 1.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = DataStandardization.class)
public class StdMax implements DataStandardization {

    public static final String name = "Maximum";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Dataset<? extends Instance> optimize(Dataset<? extends Instance> dataset) {
        double[] maxVal = new double[dataset.attributeCount()];
        double min, max;
        Dataset<? extends Instance> opt = dataset.duplicate();
        //find max|data_{ij}| value
        for (Entry<Integer, Attribute> entry : dataset.getAttributes().entrySet()) {
            min = Math.abs(entry.getValue().statistics(AttrNumStats.MIN));
            max = entry.getValue().statistics(AttrNumStats.MAX);
            if (min > max) {
                maxVal[entry.getKey()] = min;
            } else {
                maxVal[entry.getKey()] = max;
            }
        }

        double value;
        for (int i = 0; i < dataset.size(); i++) {
            for (int j = 0; j < dataset.attributeCount(); j++) {
                //System.out.println("max val = " + maxVal[j]);
                value = dataset.get(i, j) / maxVal[j];
                //System.out.println("[" + i + "," + j + "] = " + value);
                opt.set(i, j, value);
            }
        }

        return opt;
    }

}
