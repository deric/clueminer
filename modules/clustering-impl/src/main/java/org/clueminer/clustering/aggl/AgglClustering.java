/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.clustering.aggl;

import java.util.AbstractQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import org.clueminer.math.MatrixVector;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.math.matrix.SymmetricMatrix;
import org.openide.util.Exceptions;

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
    public static Matrix rowSimilarityMatrix(Matrix m, Distance dm) {
        return rowSimilarityMatrix(m, dm, null);
    }

    /**
     * Computes and returns the similarity matrix for {@code m} using the
     * specified similarity function. Moreover matrix values will be stored in
     * queue.
     *
     * @TODO: consider parallel computation of distances
     *
     * @param m
     * @param dm
     * @param queue queue to store computed number
     * @return
     */
    public static Matrix rowSimilarityMatrix(Matrix m, Distance dm, AbstractQueue<Element> queue) {
        Matrix similarityMatrix;
        double dist;
        if (dm.isSymmetric()) {

            similarityMatrix = new SymmetricMatrix(m.rowsCount(), m.rowsCount());
            for (int i = 0; i < m.rowsCount(); ++i) {
                for (int j = i + 1; j < m.rowsCount(); ++j) {
                    dist = dm.measure(m.getRowVector(i), m.getRowVector(j));
                    similarityMatrix.set(i, j, dist);
                    // when printing lower part of matrix this indexes should match
                    if (queue != null) {
                        queue.add(new Element(dist, i, j));
                    }
                }
            }
        } else {
            double dist2;
            MatrixVector vi, vj;
            similarityMatrix = new JMatrix(m.rowsCount(), m.rowsCount());
            for (int i = 0; i < m.rowsCount(); ++i) {
                for (int j = i + 1; j < m.rowsCount(); ++j) {
                    /**
                     * measure is not symmetrical, we have to compute distance
                     * from A to B and from B to A
                     */
                    vi = m.getRowVector(i);
                    vj = m.getRowVector(j);
                    dist = dm.measure(vi, vj);
                    similarityMatrix.set(i, j, dist);
                    dist2 = dm.measure(vj, vi);
                    similarityMatrix.set(j, i, dist2);
                    if (queue != null) {
                        queue.add(new Element(dist, i, j));
                        queue.add(new Element(dist2, j, i));
                    }
                }
            }
        }
        return similarityMatrix;
    }

    /**
     * We expect distance measure to be symmetrical
     *
     * @param m
     * @param dm
     * @param queue
     * @param threads
     * @return
     */
    public static Matrix rowSimilarityMatrixParSym(final Matrix m, final Distance dm, final AbstractQueue<Element> queue, int threads) {
        final Matrix similarityMatrix = new SymmetricMatrix(m.rowsCount(), m.rowsCount());
        CyclicBarrier barrier = new CyclicBarrier(threads);
        Thread[] run = new Thread[threads];
        for (int t = 0; t < threads; t++) {
            run[t] = new Thread(new RowSimThread(m, dm, queue, t, threads, similarityMatrix, barrier));
            run[t].start();
        }

        try {
            for (int i = 0; i < threads; i++) {
                run[i].join();
            }
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return similarityMatrix;
    }

    public static Matrix rowSimilarityMatrixParSymLock(final Matrix m, final Distance dm, final AbstractQueue<Element> queue, int threads) {
        final Matrix similarityMatrix = new SymmetricMatrix(m.rowsCount(), m.rowsCount());
        CyclicBarrier barrier = new CyclicBarrier(threads);
        ReentrantLock lock = new ReentrantLock();
        Thread[] run = new Thread[threads];
        for (int t = 0; t < threads; t++) {
            run[t] = new Thread(new RowSimThread2(m, dm, queue, t, threads, similarityMatrix, barrier, lock));
            run[t].start();
        }

        try {
            for (int i = 0; i < threads; i++) {
                run[i].join();
            }
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
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
    public static Matrix columnSimilarityMatrix(Matrix m, Distance dm) {
        return columnSimilarityMatrix(m, dm, null);
    }

    static Matrix columnSimilarityMatrix(Matrix m, Distance dm, AbstractQueue<Element> queue) {
        Matrix similarityMatrix;
        double dist;
        if (dm.isSymmetric()) {
            similarityMatrix = new SymmetricMatrix(m.columnsCount(), m.columnsCount());
            for (int i = 0; i < m.columnsCount(); ++i) {
                for (int j = i + 1; j < m.columnsCount(); ++j) {
                    dist = dm.measure(m.getColumnVector(i), m.getColumnVector(j));
                    similarityMatrix.set(i, j, dist);
                    if (queue != null) {
                        // when printing lower part of matrix this indexes should match
                        queue.add(new Element(dist, i, j));
                    }
                }
            }
        } else {
            double dist2;
            MatrixVector vi, vj;
            similarityMatrix = new JMatrix(m.columnsCount(), m.columnsCount());
            for (int i = 0; i < m.columnsCount(); ++i) {
                for (int j = i + 1; j < m.columnsCount(); ++j) {
                    /**
                     * measure is not symmetrical, we have to compute distance
                     * from A to B and from B to A
                     */
                    vi = m.getColumnVector(i);
                    vj = m.getColumnVector(j);
                    dist = dm.measure(vi, vj);
                    similarityMatrix.set(i, j, dist);
                    //inversed distance
                    dist2 = dm.measure(vj, vi);
                    similarityMatrix.set(j, i, dist2);
                    if (queue != null) {
                        queue.add(new Element(dist, i, j));
                        queue.add(new Element(dist2, j, i));
                    }
                }
            }
        }
        return similarityMatrix;
    }

}
