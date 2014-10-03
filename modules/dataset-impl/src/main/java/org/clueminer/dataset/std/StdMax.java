package org.clueminer.dataset.std;

import java.util.Map.Entry;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.DataStandardization;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
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

    public double[][] optimize(double[][] data, int m, int n) {
        double[] maxVal = new double[n];
        int i, j;
        double[][] res = new double[m][n];
        double value;
        /**
         * finds max in each column
         */
        for (j = 0; j < n; j++) {
            for (i = 0; i < m; i++) {
                value = Math.abs(data[i][j]);
                if (value > maxVal[j]) {
                    maxVal[j] = value;
                }
            }
        }

        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {
                res[i][j] = data[i][j] / maxVal[j];
            }
        }
        return res;
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

        for (int i = 0; i < dataset.size(); i++) {
            for (int j = 0; j < dataset.attributeCount(); j++) {
                opt.setAttributeValue(j, i, dataset.getAttributeValue(j, i) / maxVal[j]);
            }
        }

        return opt;
    }

}
