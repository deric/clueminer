package org.clueminer.utils;

import org.clueminer.dataset.api.DataStandardization;
import org.clueminer.dataset.api.DataStandardizationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.math.Standardisation;
import org.clueminer.math.StandardisationFactory;
import org.clueminer.math.matrix.JMatrix;
import static org.clueminer.std.Scaler.logScale;

/**
 *
 * @author Tomas Barton
 */
public class DataScaler {

    public static Matrix standartize(Dataset<? extends Instance> dataset, String method, boolean logScale) {
        DataStandardizationFactory sf = DataStandardizationFactory.getInstance();
        DataStandardization std = sf.getProvider(method);

        if (std == null) {
            throw new RuntimeException("Standartization method " + std + " was not found");
        }

        Matrix stdarr = std.optimize(dataset);
        if (logScale) {
            //stdarr = logScale(stdarr, m, n);
        }
        return stdarr;
    }

}
