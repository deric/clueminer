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
package org.clueminer.clustering.spectral;

import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.SymmetricMatrixDiag;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author mikusmi1
 */
@ServiceProvider(service = MatrixConvertor.class)
public class EpsNMatrix<E extends Instance> implements MatrixConvertor<E> {

    private static final String NAME = "epsilon-neighborhood matrix";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Matrix buildMatrix(Matrix similarityMatrix, Props params) {

        double eps = params.getDouble("Eps", 0.5);

        if (eps < 0.0 || eps > 1.0) {
            throw new RuntimeException("Epsilon value '" + eps + "' should be between 0.0 and 1.0");
        }

        SymmetricMatrixDiag epsMatrix = new SymmetricMatrixDiag(similarityMatrix.rowsCount(), similarityMatrix.columnsCount(), 0);

        for (int i = 0; i < similarityMatrix.rowsCount(); i++) {
            for (int j = 0; j < similarityMatrix.columnsCount(); j++) {

                // Set diagonal to 1 - exp(distance=0) = 1 (Gaussian similarity function)
                if (i == j) {
                    epsMatrix.set(i, j, 1);
                } else {
                    double similarity = similarityMatrix.get(i, j);
                    if (similarity > eps) {
                        epsMatrix.set(i, j, similarity);
                    }
                }
            }
        }
        return epsMatrix;
    }
}
