/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.dgram.vis;

import java.awt.Image;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.clustering.gui.ClusteringVisualization;
import org.clueminer.clustering.gui.ClusteringVisualizationFactory;
import org.clueminer.clustering.gui.VisualizationTask;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class ImageFactory<E extends Instance, C extends Cluster<E>> {

    private DendrogramRenderer[] workers;
    private static ImageFactory instance;
    private BlockingQueue<VisualizationTask> queue;
    private static final Logger LOG = LoggerFactory.getLogger(ImageFactory.class);
    private int workerCnt = 0;
    private ExecutorService executor = Executors.newFixedThreadPool(5);

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
        executor = Executors.newFixedThreadPool(numWorkers);
        workers = new DendrogramRenderer[numWorkers];
        LOG.info("intializing {} workers", numWorkers);
        ensure(numWorkers);
    }

    public ImageFactory ensure(int workersNum) {
        if (workerCnt < workersNum) {
            executor = Executors.newFixedThreadPool(workersNum);
        }

        return instance;
    }

    public void generateImage(Clustering<E, C> clustering, Props prop, DendrogramVisualizationListener listener, DendrogramMapping mapping) {
        //ensure at least one worker
        ensure(1);
        VisualizationTask task = new VisualizationTask(clustering, prop, listener, mapping);

        ClusteringVisualizationFactory cf = ClusteringVisualizationFactory.getInstance();
        ClusteringVisualization<Image> cv = cf.getProvider(prop.get("clustering.visualization", "Dendrogram"));

        executor.submit(cv);

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
    protected VisualizationTask getTask() throws InterruptedException {
        return queue.take();
    }

    /**
     * Stop workers and free resources
     */
    public void shutdown() {
        LOG.info("stopping {} workers", workerCnt);
        if (workerCnt > 0) {
            for (DendrogramRenderer worker : workers) {
                if (worker != null) {
                    worker.stop();
                }
                workerCnt--;
            }
        }
    }

}
