package org.clueminer.dataset.std;

import org.clueminer.dataset.api.DataStandardization;
import org.clueminer.dataset.api.DataStandardizationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.std.StdNone;
import org.clueminer.std.StdScale;

/**
 *
 * @author Tomas Barton
 */
public class DataScaler {

    public static Dataset<? extends Instance> standartize(Dataset<? extends Instance> dataset, String method, boolean logScale) {
        DataStandardizationFactory sf = DataStandardizationFactory.getInstance();
        Dataset<? extends Instance> res;
        if (method.equals(StdNone.name)) {
            //nothing to optimize
            return dataset;
        }

        DataStandardization std = sf.getProvider(method);

        if (std == null) {
            throw new RuntimeException("Standartization method " + std + " was not found");
        }

        res = std.optimize(dataset);
        if (logScale) {
            StdScale scale = new StdScale();
            scale.setTargetMin(1);

            //double min = ;
            double max = 0.0;
            double min = 0.0;
            scale.setTargetMax(-min + max + 1);

            for (int i = 0; i < res.size(); i++) {
                for (int j = 0; j < res.attributeCount(); j++) {
                    res.set(i, j, Math.log(res.get(i, j)));
                }
            }
            //stdarr = logScale(stdarr, m, n);
        }
        return res;
    }

}
