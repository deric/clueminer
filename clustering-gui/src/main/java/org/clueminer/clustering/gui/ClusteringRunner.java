package org.clueminer.clustering.gui;

import org.clueminer.approximation.api.DataTransform;
import org.clueminer.approximation.api.DataTransformFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.AlgorithmParameters;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringRunner implements Runnable {

    private ClusteringDialog config = null;
    private ClusterAnalysis analysis;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);
    private static String rawData = "-- no transformation --";
    private boolean preprocessingFinished = false;

    public ClusteringRunner(ClusterAnalysis clust, ClusteringDialog config) {
        this.analysis = clust;
        this.config = config;
    }

    @Override
    public void run() {
        AlgorithmParameters params = config.getParams();


        String datasetTransform = params.getString("dataset");
        System.out.println("using: " + datasetTransform);


        if (!analysis.hasDataset()) {
            throw new RuntimeException("missing dataset!");
        }

        Dataset<? extends Instance> data = analysis.getDataset();
        if (!datasetTransform.equals(rawData)) {
            Dataset<? extends Instance> transform;
            //check if there's preloaded dataset available
            transform = data.getChild(datasetTransform);
            if (transform == null) {
                //run analysis and wait
                final Object lock = new Object();

                runPreprocessing(lock, data, datasetTransform);

                synchronized (lock) {
                    while (!preprocessingFinished) {
                        try {
                            lock.wait();
                        } catch (InterruptedException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
                throw new RuntimeException("dataset is not available yet");
            }
            analysis.setDataset(transform);
        }

        analysis.execute(params);
    }

    private void runPreprocessing(final Object lock, final Dataset<? extends Instance> data, String datasetTransform) {

        DataTransformFactory df = DataTransformFactory.getDefault();
        final DataTransform trans = df.getProvider(datasetTransform);
        final Dataset<? extends Instance> output = trans.createDefaultOutput(data);

        final ProgressHandle ph = ProgressHandleFactory.createHandle("Running preprocessing");

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
                    preprocessingFinished = true;
                    lock.notifyAll();
                }
            }
        });
        taskAnalyze.schedule(0);
    }
}
