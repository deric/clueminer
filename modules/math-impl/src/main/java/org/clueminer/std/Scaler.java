/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.std;

import org.clueminer.math.Matrix;
import org.clueminer.math.Standardisation;
import org.clueminer.math.StandardisationFactory;
import org.clueminer.math.matrix.JMatrix;

/**
 * Normalize data arrays. For normalizing datasets see DataScaler.
 *
 * @author Tomas Barton
 */
public class Scaler {

    /**
     * Provider for skipping normalization
     */
    public static final String NONE = "None";

    public static Matrix standartize(double[][] datasetArray, String method, boolean logScale) {
        StandardisationFactory sf = StandardisationFactory.getInstance();
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
