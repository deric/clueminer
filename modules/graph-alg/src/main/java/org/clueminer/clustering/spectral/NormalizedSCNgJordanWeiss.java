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

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.SymmetricMatrixDiag;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mikusmi1
 */
@ServiceProvider(service = EigenVectorConvertor.class)
public class NormalizedSCNgJordanWeiss<E extends Instance> implements EigenVectorConvertor<E> {

    private static final Logger LOG = LoggerFactory.getLogger(SpectralClustering.class);
    private static final String NAME = "Normalized SP Ng&Jordan&Weiss";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Dataset<E> buildEigVecsMatrix(Matrix neighborhoodMatrix, int k) {
        LOG.debug("computing laplacian matrix");
        SymmetricMatrixDiag laplacianMatrix = buildSymmetricNormalizedLaplacianMatrix(neighborhoodMatrix);
        LOG.info("laplacian matrix rank: {}", laplacianMatrix.rank());

        LOG.info("2) Decomposition - compute eigenvectors");
        LOG.debug("computing eigen vectors");
        return computeEigenVectorsU(laplacianMatrix, k);
    }

    private SymmetricMatrixDiag buildSymmetricNormalizedLaplacianMatrix(Matrix similarityMatrix) {
        //LOG.debug("Mat: {}/{}", similarityMatrix.rowsCount(), similarityMatrix.columnsCount());
        SymmetricMatrixDiag laplacianMatrix = new SymmetricMatrixDiag(similarityMatrix.rowsCount(), similarityMatrix.columnsCount(), 0);
        for (int i = 0; i < similarityMatrix.rowsCount(); i++) {
            for (int j = 0; j < similarityMatrix.columnsCount(); j++) {

                int deg1 = 0;
                double sumWeight1 = 0.0;
                double sumWeight2 = 0.0;
                for (int columnIndex = 0; columnIndex < similarityMatrix.columnsCount(); columnIndex++) {
                    if (columnIndex != i) {
                        double weight1 = similarityMatrix.get(i, columnIndex);
                        if (weight1 != 0.0) {
                            deg1 += 1;
                            sumWeight1 += weight1;
                        }
                    }

                    if (columnIndex != j) {
                        double weight2 = similarityMatrix.get(columnIndex, j);
                        if (weight2 != 0.0) {
                            sumWeight2 += weight2;
                        }
                    }

                }

                if (i == j && deg1 != 0) {
                    laplacianMatrix.set(i, j, 1);
                    continue;
                }

                double weight = similarityMatrix.get(i, j);
                double normalization = weight / (double) Math.sqrt(sumWeight1 * sumWeight2);
                laplacianMatrix.set(i, j, 0.0 - normalization);
            }
        }
        return laplacianMatrix;
    }

    private Dataset<E> computeEigenVectorsU(SymmetricMatrixDiag laplacianMatrix, int k) {
        Matrix eigMatrix = laplacianMatrix.eig().getV();
        LOG.info("eigen matrix rank: {}", eigMatrix.rank());

        // Convert eigen matrix to dataset
        double[][] eigArrayTemp = eigMatrix.getArrayCopy();
        double[][] eigArray = new double[eigArrayTemp.length][k];

        for (int i = 0; i < eigArrayTemp.length; i++) {
            for (int j = 0; j < k; j++) {
                eigArray[i][j] = eigArrayTemp[i][j];
            }
        }

        double[][] uMatrix = new double[eigArrayTemp.length][k];

        double sumOptRoot[] = new double[uMatrix.length];
        for (int i = 0; i < uMatrix.length; i++) {
            double sumOptRow = 0.0;
            for (int j = 0; j < k; j++) {
                sumOptRow += Math.pow(eigArray[i][j], 2);
            }
            sumOptRoot[i] = Math.sqrt(sumOptRow);
        }

        for (int i = 0; i < uMatrix.length; i++) {
            for (int j = 0; j < k; j++) {
                double d = eigArray[i][j] / sumOptRoot[i];
                uMatrix[i][j] = d;
            }
        }

        return new ArrayDataset<>(uMatrix);
    }
}
