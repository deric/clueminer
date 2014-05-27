package org.clueminer.clustering.aggl;

import java.util.AbstractQueue;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.math.matrix.SymmetricMatrix;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

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
        int n = 0;
        ProgressHandle ph = ProgressHandleFactory.createHandle("Computing row similarity matrix (" + m.rowsCount() + " x " + m.columnsCount() + ")");
        int total = m.rowsCount() * m.columnsCount() / 2;
        ph.start(total);
        if (dm.isSymmetric()) {

            similarityMatrix = new SymmetricMatrix(m.rowsCount(), m.rowsCount());
            for (int i = 0; i < m.rowsCount(); ++i) {
                for (int j = i + 1; j < m.rowsCount(); ++j) {
                    //double similarity =  Similarity.getSimilarity(similarityFunction, m.getRowVector(i), m.getRowVector(j));
                    similarityMatrix.set(i, j, dm.measure(m.getRowVector(i), m.getRowVector(j)));
                    ph.progress(n++);
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
                    similarity = dm.measure(m.getRowVector(j), m.getRowVector(i));
                    similarityMatrix.set(j, i, similarity);
                    ph.progress(n++);
                }
            }
        }
        return similarityMatrix;
    }

    /**
     * Computes and returns the similarity matrix for {@code m} using the
     * specified similarity function. Moreover matrix values will be stored in
     * queue.
     *
     * @param m
     * @param dm
     * @param queue queue to store computed number
     * @return
     */
    public static Matrix rowSimilarityMatrix(Matrix m, DistanceMeasure dm, AbstractQueue<Element> queue) {
        Matrix similarityMatrix;
        int n = 0;
        double distance;
        ProgressHandle ph = ProgressHandleFactory.createHandle("Computing row similarity matrix (" + m.rowsCount() + " x " + m.columnsCount() + ")");
        int total = (m.rowsCount() - 1) * m.rowsCount() / 2;
        ph.start(total);
        if (dm.isSymmetric()) {

            similarityMatrix = new SymmetricMatrix(m.rowsCount(), m.rowsCount());
            for (int i = 0; i < m.rowsCount(); ++i) {
                for (int j = i + 1; j < m.rowsCount(); ++j) {
                    distance = dm.measure(m.getRowVector(i), m.getRowVector(j));
                    similarityMatrix.set(i, j, distance);
                    //System.out.println("#" + n + " (" + i + ", " + j + ") = " + distance);
                    // when printing lower part of matrix this indexes should match
                    queue.add(new Element(distance, i, j));
                    ph.progress(n++);
                }
            }
        } else {
            similarityMatrix = new JMatrix(m.rowsCount(), m.rowsCount());
            for (int i = 0; i < m.rowsCount(); ++i) {
                for (int j = i + 1; j < m.rowsCount(); ++j) {
                    /**
                     * measure is not symmetrical, we have to compute distance
                     * from A to B and from B to A
                     */
                    distance = dm.measure(m.getRowVector(i), m.getRowVector(j));
                    similarityMatrix.set(i, j, distance);
                    queue.add(new Element(distance, i, j));
                    distance = dm.measure(m.getRowVector(j), m.getRowVector(i));
                    similarityMatrix.set(j, i, distance);
                    queue.add(new Element(distance, j, i));
                    ph.progress(n++);
                }
            }
        }
        ph.finish();
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
