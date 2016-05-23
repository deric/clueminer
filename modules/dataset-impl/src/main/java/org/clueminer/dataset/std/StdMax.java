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
 * @param <E>
 */
@ServiceProvider(service = DataStandardization.class)
public class StdMax<E extends Instance> implements DataStandardization<E> {

    public static final String name = "Maximum";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Dataset<E> optimize(Dataset<E> dataset) {
        double[] maxVal = new double[dataset.attributeCount()];
        double min, max;
        Dataset<E> opt = (Dataset<E>) dataset.duplicate();
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
        Instance orig;
        for (int i = 0; i < dataset.size(); i++) {
            for (int j = 0; j < dataset.attributeCount(); j++) {
                //System.out.println("max val = " + maxVal[j]);
                value = dataset.get(i, j) / maxVal[j];
                //System.out.println("[" + i + "," + j + "] = " + value);
                opt.set(i, j, value);
            }
            orig = dataset.get(i);
            opt.get(i).setClassValue(orig.classValue());
            opt.get(i).setId(orig.getId());
            opt.get(i).setName(orig.getName());
            opt.get(i).setAncestor(orig);
            opt.changedClass(i, orig.classValue(), null);
        }

        return opt;
    }

}
