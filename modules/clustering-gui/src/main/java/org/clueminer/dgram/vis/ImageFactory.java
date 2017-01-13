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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.dataset.api.Instance;
import org.openide.util.RequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(ImageFactory.class);
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
        LOG.info("intializing {} workers", numWorkers);
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
        LOG.info("stopping {} workers", workerCnt);
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
