package org.clueminer.dgram.vis;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tomas Barton
 */
public class ImageFactory {

    private ImageWorker[] workers;
    private static ImageFactory instance;
    private BlockingQueue<ImageTask> queue;
    private static final RequestProcessor RP = new RequestProcessor("Dendrogram image preview");
    private ExecutorService threadPoolExecutor;
    private final Logger logger = Logger.getLogger(ImageFactory.class.getName());

    public static ImageFactory getInstance(int numWorkers) {
        if (instance == null) {
            instance = new ImageFactory(numWorkers);
        }
        //TODO: ensure scaling workers upto requested count
        return instance;
    }

    private ImageFactory(int workers) {
        initWorkers(workers);
    }

    private void initWorkers(int numWorkers) {
        queue = new LinkedBlockingQueue<>(5);
        workers = new ImageWorker[numWorkers];
        logger.log(Level.INFO, "intializing {0} workers", numWorkers);

        for (int i = 0; i < workers.length; i++) {
            //each workers has its own vizualization components
            workers[i] = new ImageWorker(this);
            //threadPoolExecutor.submit(workers[i]);
            RP.post(workers[i]);
        }

    }

    public void generateImage(Clustering<? extends Cluster> clustering, int width, int height, DendrogramVisualizationListener listener, DendrogramMapping mapping) {
        ImageTask task = new ImageTask(clustering, width, height, listener, mapping);
        logger.info("adding task");
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
        //TODO
    }

}
