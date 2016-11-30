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
package org.clueminer.transform.ui;

import org.clueminer.approximation.api.DataTransform;
import org.clueminer.approximation.api.DataTransformFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.flow.api.FlowNode;
import org.clueminer.transform.DatasetTransformation;
import org.clueminer.utils.Props;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
@ServiceProvider(service = FlowNode.class)
public class DatasetTransformationFlow implements FlowNode {

    private final Class[] inputs = new Class[]{Dataset.class};
    private final Class[] outputs = new Class[]{Dataset.class};
    private Logger LOG = LoggerFactory.getLogger(DatasetTransformationFlow.class);
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);
    private boolean preprocessingFinished = false;
    private Dataset<? extends Instance> transform;

    @Override
    public String getName() {
        return DatasetTransformation.NAME;
    }

    @Override
    public Object[] getInputs() {
        return inputs;
    }

    @Override
    public Object[] getOutputs() {
        return outputs;
    }

    @Override
    public Object[] execute(Object[] input, Props params) {
        checkInputs(input);
        Object[] ret = new Object[1];

        Dataset<? extends Instance> data = (Dataset<? extends Instance>) input[0];
        LOG.info("dataset size: {}", data.size());
        LOG.info("dataset has {} attributes", data.attributeCount());

        if (data.isEmpty() || data.attributeCount() == 0) {
            throw new RuntimeException("dataset is empty!");
        }
        String transformation = params.get("transformation", "");

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
        ret[0] = transform;

        return ret;
    }

    private void checkInputs(Object[] in) {
        if (in.length != inputs.length) {
            throw new RuntimeException("expected " + inputs.length + " input(s), got " + in.length);
        }
        //type check
        int i = 0;
        /* for (Object obj : in) {            if (!obj.getClass().equals(inputs[i].getClass())) {
                throw new RuntimeException("expected " + inputs[i].getClass().toString() + " input(s), got " + in.getClass().toString());
            }
            i++;
        } */

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
