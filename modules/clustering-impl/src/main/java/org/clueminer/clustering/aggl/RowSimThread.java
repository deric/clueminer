package org.clueminer.clustering.aggl;

import java.util.AbstractQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class RowSimThread implements Runnable {

    private final Matrix m;
    private final DistanceMeasure dm;
    private final AbstractQueue<Element> queue;
    private final int threadId;
    private final int threads;
    private final Matrix similarityMatrix;
    private final CyclicBarrier barrier;

    public RowSimThread(Matrix m, DistanceMeasure dm, AbstractQueue<Element> queue,
            int threadId, int threads, Matrix similarityMatrix, CyclicBarrier barrier) {
        this.m = m;
        this.dm = dm;
        this.queue = queue;
        this.threadId = threadId;
        this.threads = threads;
        this.similarityMatrix = similarityMatrix;
        this.barrier = barrier;
    }

    /**
     * Thread for computing similarities, we start from row with @threadId and
     * increment by number of threads. At the end we have to wait for other
     * threads to finish computations
     */
    @Override
    public void run() {
        double dist;
        for (int i = threadId; i < m.rowsCount(); i += threads) {
            for (int j = i + 1; j < m.rowsCount(); ++j) {
                dist = dm.measure(m.getRowVector(i), m.getRowVector(j));
                //System.out.println("{" + threadId + "} [" + i + ", " + j + "] -> " + dist);
                similarityMatrix.set(i, j, dist);
                // when printing lower part of matrix this indexes should match
                if (queue != null) {
                    queue.add(new Element(dist, i, j));
                }
            }
        }
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
