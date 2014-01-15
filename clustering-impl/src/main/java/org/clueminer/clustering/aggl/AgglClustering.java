package org.clueminer.clustering.aggl;

import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.math.matrix.SymmetricMatrix;

/**
 * Agglomerative clustering methods
 *
 * @author Tomas Barton
 */
public class AgglClustering {

    /**
     * Computes and returns the similarity matrix for {@code m} using the
     * specified similarity function
     *
     * @param m
     * @param dm
     * @return
     */
    public static Matrix rowSimilarityMatrix(Matrix m, DistanceMeasure dm) {
        Matrix similarityMatrix;

        if (dm.isSymmetric()) {
            similarityMatrix = new SymmetricMatrix(m.rowsCount(), m.rowsCount());
            for (int i = 0; i < m.rowsCount(); ++i) {
                for (int j = i + 1; j < m.rowsCount(); ++j) {
                    //double similarity =  Similarity.getSimilarity(similarityFunction, m.getRowVector(i), m.getRowVector(j));
                    similarityMatrix.set(i, j, dm.measure(m.getRowVector(i), m.getRowVector(j)));
                }
            }
        } else {
            double similarity;
            similarityMatrix = new JMatrix(m.rowsCount(), m.rowsCount());
            for (int i = 0; i < m.rowsCount(); ++i) {
                for (int j = i + 1; j < m.rowsCount(); ++j) {
                    /**
                     * measure is not symmetrical, we have to compute distance
                     * from A to B and from B to A
                     */
                    similarity = dm.measure(m.getRowVector(i), m.getRowVector(j));
                    similarityMatrix.set(i, j, similarity);
                    similarityMatrix.set(j, i, similarity);
                }
            }
        }
        return similarityMatrix;
    }

    /**
     * Computes similarity matrix for columns
     *
     * @param m
     * @param dm
     * @return
     */
    public static Matrix columnsSimilarityMatrix(Matrix m, DistanceMeasure dm) {
        Matrix similarityMatrix;

        if (dm.isSymmetric()) {
            similarityMatrix = new SymmetricMatrix(m.columnsCount(), m.columnsCount());
            for (int i = 0; i < m.columnsCount(); ++i) {
                for (int j = i + 1; j < m.columnsCount(); ++j) {
                    similarityMatrix.set(i, j, dm.measure(m.getColumnVector(i), m.getColumnVector(j)));
                }
            }
        } else {
            double similarity;
            similarityMatrix = new JMatrix(m.columnsCount(), m.columnsCount());
            for (int i = 0; i < m.columnsCount(); ++i) {
                for (int j = i + 1; j < m.columnsCount(); ++j) {
                    /**
                     * measure is not symmetrical, we have to compute distance
                     * from A to B and from B to A
                     */
                    similarity = dm.measure(m.getColumnVector(i), m.getColumnVector(j));
                    similarityMatrix.set(i, j, similarity);
                    similarityMatrix.set(j, i, similarity);
                }
            }
        }
        return similarityMatrix;

    }

}
