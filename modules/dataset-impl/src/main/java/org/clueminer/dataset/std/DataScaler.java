package org.clueminer.dataset.std;

import org.clueminer.dataset.api.DataStandardization;
import org.clueminer.dataset.api.DataStandardizationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import static org.clueminer.std.Scaler.logScale;
import org.clueminer.std.StdNone;

/**
 *
 * @author Tomas Barton
 */
public class DataScaler {

    public static Dataset<? extends Instance> standartize(Dataset<? extends Instance> dataset, String method, boolean logScale) {
        DataStandardizationFactory sf = DataStandardizationFactory.getInstance();
        if (method.equals(StdNone.name)) {
            //nothing to optimize
            return dataset;
        }

        DataStandardization std = sf.getProvider(method);

        if (std == null) {
            throw new RuntimeException("Standartization method " + std + " was not found");
        }

        Dataset<? extends Instance> stdarr = std.optimize(dataset);
        if (logScale) {
            //stdarr = logScale(stdarr, m, n);
        }
        return null;
    }

}
