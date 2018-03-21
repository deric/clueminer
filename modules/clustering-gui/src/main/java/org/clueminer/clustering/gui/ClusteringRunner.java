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
package org.clueminer.clustering.gui;

import org.clueminer.approximation.api.DataTransform;
import org.clueminer.approximation.api.DataTransformFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringRunner implements Runnable {

    private ClusteringDialog config = null;
    private final ClusterAnalysis analysis;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);
    private boolean preprocessingFinished = false;
    private Dataset<? extends Instance> transform;
    private static final Logger LOG = LoggerFactory.getLogger(ClusteringRunner.class);

    public ClusteringRunner(ClusterAnalysis clust, ClusteringDialog config) {
        this.analysis = clust;
        this.config = config;
    }

    @Override
    public void run() {
        Dataset<? extends Instance> dataset;
        Props params = config.getParams();

        String datasetTransform = params.get("transformations", "");
        String[] trans = null;
        LOG.info("using trasformations: {}", datasetTransform);
        if (datasetTransform.length() > 0) {
            trans = datasetTransform.split(",");
        }

        if (!analysis.hasDataset()) {
            throw new RuntimeException("missing dataset!");
        }

        Dataset<? extends Instance> data = analysis.getDataset();
        LOG.info("dataset size: {}", data.size());
        LOG.info("dataset has {} attributes", data.attributeCount());

        if (data.isEmpty() || data.attributeCount() == 0) {
            throw new RuntimeException("dataset is empty!");
        }
        if (trans != null) {
            for (String transformation : trans) {
                //make sure we don't have old data
                transform = null;
                //check if there's preloaded dataset available
                transform = data.getChild(transformation);
                if (transform == null) {
                    preprocessingFinished = false;
                    //run analysis and wait
                    final Object lock = new Object();

                    runPreprocessing(lock, data, transformation);

                    synchronized (lock) {
                        while (!preprocessingFinished) {
                            try {
                                lock.wait();
                            } catch (InterruptedException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }
                }
                //System.out.println("trasformed dataset " + transform.getClass().toString() + " name: " + transform.getName() + ", size = " + transform.size());

                //wait until real data are loaded
                if ((transform instanceof Dataset) && transform.isEmpty()) {
                    LOG.info("waiting for data");
                    while ((transform = data.getChild(datasetTransform)) == null) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
                data = transform;
            }
            dataset = transform;
        } else {
            dataset = data;
        }
        analysis.execute(params, dataset);
    }

    private void runPreprocessing(final Object lock, final Dataset<? extends Instance> data, String datasetTransform) {

        DataTransformFactory df = DataTransformFactory.getInstance();
        final DataTransform trans = df.getProvider(datasetTransform);
        final Dataset<? extends Instance> output = trans.createDefaultOutput(data);

        final ProgressHandle ph = ProgressHandle.createHandle("Running preprocessing");

        final RequestProcessor.Task taskAnalyze = RP.create(new Runnable() {
            @Override
            public void run() {
                trans.analyze(data, output, ph);
            }
        });
        taskAnalyze.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(Task task) {
                synchronized (lock) {
                    LOG.info("preprocessing finished.");
                    LOG.info("output dataset " + output.getClass().toString() + " name: " + output.getName() + ", size = " + output.size());
                    transform = output;

                    preprocessingFinished = true;
                    lock.notifyAll();
                }
            }
        });
        taskAnalyze.schedule(0);
    }
}
