package org.clueminer.std;

import org.clueminer.math.matrix.JMatrix;
import org.clueminer.math.Matrix;
import org.clueminer.math.Standardisation;

/**
 *
 * @author Tomas Barton
 */
public class Scaler {

    public static Matrix standartize(double[][] datasetArray, String method, boolean logScale) {
        StandardisationFactory sf = StandardisationFactory.getDefault();
        Standardisation std = sf.getProvider(method);

        if (std == null) {
            throw new RuntimeException("Standartization method " + std + " was not found");
        }

        int m = datasetArray.length;
        int n = datasetArray[0].length;
        
        double[][] stdarr = std.optimize(datasetArray, m, n);
        if (logScale) {
            stdarr = logScale(stdarr, m, n);
        }
        return new JMatrix(stdarr, m, n);
    }

    public static double[][] logScale(double[][] stdarr, int m, int n) {
        //logaritmuj - numbers must be positive!!
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (stdarr[i][j] > max) {
                    max = stdarr[i][j];
                } else if (stdarr[i][j] < min) {
                    min = stdarr[i][j];
                }
            }
        }
        StdScale scale = new StdScale();
        scale.setTargetMin(1);
        scale.setTargetMax(-min + max + 1);
        stdarr = scale.optimize(stdarr, m, n);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                stdarr[i][j] = Math.log(stdarr[i][j]);
            }
        }
        return stdarr;
    }
}
