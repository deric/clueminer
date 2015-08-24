package org.clueminer.dgram.vis;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.dataset.api.Instance;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class ImageFactory<E extends Instance, C extends Cluster<E>> {

    private ImageWorker[] workers;
    private static ImageFactory instance;
    private BlockingQueue<ImageTask> queue;
    private static final RequestProcessor RP = new RequestProcessor("Dendrogram image preview");
    private static final Logger logger = Logger.getLogger(ImageFactory.class.getName());
    private int workerCnt = 0;

    public static ImageFactory getInstance() {
        if (instance == null) {
            instance = new ImageFactory(5);
        }
        //TODO: ensure scaling workers upto requested count
        return instance;
    }

    private ImageFactory(int workers) {
        initWorkers(workers);
    }

    private void initWorkers(int numWorkers) {
        queue = new LinkedBlockingQueue<>();
        workers = new ImageWorker[numWorkers];
        logger.log(Level.INFO, "intializing {0} workers", numWorkers);
        ensure(numWorkers);
    }

    public ImageFactory ensure(int workersNum) {
        if (workerCnt < workersNum) {
            setCapacity(workersNum);
            if (workerCnt < 0) {
                workerCnt = 0;
            }
            for (int i = workerCnt; i < workersNum; i++) {
                //each workers has its own vizualization components
                workers[i] = new ImageWorker(this);
                RP.post(workers[i]);
                workerCnt++;
            }
        }
        return instance;
    }

    private void setCapacity(int capacity) {
        if (workers.length >= capacity) {
            return;
        }
        if (capacity < 1) {
            capacity = 1;
        }
        ImageWorker[] newWorkers = new ImageWorker[capacity];
        System.arraycopy(workers, 0, newWorkers, 0, workers.length);
        workers = newWorkers;
    }

    public void generateImage(Clustering<E, C> clustering, int width, int height, DendrogramVisualizationListener listener, DendrogramMapping mapping) {
        //ensure at least one worker
        ensure(1);
        ImageTask task = new ImageTask(clustering, width, height, listener, mapping);
        queue.add(task);
        if (workers.length == 0) {
            throw new RuntimeException("no workers are running");
        }
    }

    protected boolean hasWork() {
        return queue.size() > 0;
    }

    /**
     * Blocking call
     *
     * @return
     * @throws InterruptedException
     */
    protected ImageTask getTask() throws InterruptedException {
        return queue.take();
    }

    /**
     * Stop workers and free resources
     */
    public void shutdown() {
        logger.log(Level.INFO, "stopping {0} workers", workerCnt);
        if (workerCnt > 0) {
            for (ImageWorker worker : workers) {
                if (worker != null) {
                    worker.stop();
                }
                workerCnt--;
            }
        }
    }

}
