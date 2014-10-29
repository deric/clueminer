package org.clueminer.clustering.aggl;

import java.util.AbstractQueue;
import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class RowSimThread2 implements Runnable {

    private final Matrix m;
    private final DistanceMeasure dm;
    private final AbstractQueue<Element> queue;
    private final int threadId;
    private final int threads;
    private final Matrix similarityMatrix;
    private final CyclicBarrier barrier;
    private final ReentrantLock lock;

    public RowSimThread2(Matrix m, DistanceMeasure dm, AbstractQueue<Element> queue,
            int threadId, int threads, Matrix similarityMatrix, CyclicBarrier barrier, final ReentrantLock lock) {
        this.m = m;
        this.dm = dm;
        this.queue = queue;
        this.threadId = threadId;
        this.threads = threads;
        this.similarityMatrix = similarityMatrix;
        this.barrier = barrier;
        this.lock = lock;
    }

    /**
     * Thread for computing similarities, we start from row with @threadId and
     * increment by number of threads. At the end we have to wait for other
     * threads to finish computations
     */
    @Override
    public void run() {
        //private final ReentrantLock lock = new ReentrantLock();

        double dist;
        LinkedList<Element> cache = new LinkedList<>();
        for (int i = threadId; i < m.rowsCount(); i += threads) {
            for (int j = i + 1; j < m.rowsCount(); ++j) {
                dist = dm.measure(m.getRowVector(i), m.getRowVector(j));
                //System.out.println("{" + threadId + "} [" + i + ", " + j + "] -> " + dist);
                similarityMatrix.set(i, j, dist);
                // when printing lower part of matrix this indexes should match
                if (queue != null) {
                    //if nobody is adding to the queue, acquire the lock
                    if (lock.tryLock()) {
                        try {
                            //only one thread is adding at the same time
                            queue.add(new Element(dist, i, j));
                            //if cache is non-empty, free it
                            while (!cache.isEmpty()) {
                                queue.add(cache.remove());
                            }
                        } finally {
                            lock.unlock();
                        }
                    } else {
                        //someone is using the queue, cache the computations
                        cache.add(new Element(dist, i, j));
                    }

                }
            }
        }
        //if the cache contains some values, make sure we add them to queue
        if (!cache.isEmpty()) {
            //blocking call
            lock.lock();
            try {
                while (!cache.isEmpty()) {
                    queue.add(cache.remove());
                }
            } finally {
                lock.unlock();
            }
        }

        try {
            //System.out.println("thread " + threadId + " at barrier");
            barrier.await();
            //System.out.println("thread " + threadId + " after barrier");
        } catch (InterruptedException | BrokenBarrierException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
