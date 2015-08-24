package org.clueminer.dataset.std;

import org.clueminer.dataset.api.DataStandardization;
import org.clueminer.dataset.api.DataStandardizationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.std.StdNone;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public class DataScaler<E extends Instance> {

    public Dataset<E> standartize(Dataset<E> dataset, String method, boolean logScale) {
        DataStandardizationFactory sf = DataStandardizationFactory.getInstance();
        Dataset<E> res;
        if (method.equals(StdNone.name)) {
            //nothing to optimize
            res = dataset;
        } else {
            DataStandardization std = sf.getProvider(method);
            if (std == null) {
                throw new RuntimeException("Standartization method " + std + " was not found");
            }
            res = std.optimize(dataset);
        }
        if (logScale) {
            StdMinMax scale = new StdMinMax();
            scale.setTargetMin(1);
            double max = res.max();
            double min = res.min();
            scale.setTargetMax(-min + max + 1);
            //normalize values, so that we can apply logarithm
            res = scale.optimize(res);
            // min-max normalized dataset is just intermediate step (shifted
            // so that values are bigger than 1.0), we'll set as parent
            // the dataset from previous step (the intermediate is never used)
            res.setParent(dataset);

            for (int i = 0; i < res.size(); i++) {
                for (int j = 0; j < res.attributeCount(); j++) {
                    res.set(i, j, Math.log(res.get(i, j)));
                }
            }
        }
        return res;
    }

}
