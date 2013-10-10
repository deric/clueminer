package org.clueminer.clustering.gui;

import org.clueminer.approximation.api.DataTransform;
import org.clueminer.approximation.api.DataTransformFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.FakeDataset;
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
    private Dataset<? extends Instance> transform;

    public ClusteringRunner(ClusterAnalysis clust, ClusteringDialog config) {
        this.analysis = clust;
        this.config = config;
    }

    @Override
    public void run() {
        Dataset<? extends Instance> dataset;
        AlgorithmParameters params = config.getParams();


        String datasetTransform = params.getString("dataset");
        System.out.println("using: " + datasetTransform);


        if (!analysis.hasDataset()) {
            throw new RuntimeException("missing dataset!");
        }

        Dataset<? extends Instance> data = analysis.getDataset();
        if (!datasetTransform.equals(rawData)) {
            //make sure we don't have old data
            transform = null;
            //check if there's preloaded dataset available
            transform = data.getChild(datasetTransform);
            if (transform == null) {
                System.out.println("missing child ");
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
            }
            System.out.println("trasformed dataset " + transform.getClass().toString() + " name: " + transform.getName() + ", size = " + transform.size());

            //wait until real data are loaded
            if (transform.equals(new FakeDataset<Instance>())) {
                System.out.println("waiting for data");
                while ((transform = data.getChild(datasetTransform)) == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

            dataset = transform;
        } else {
            dataset = data;
        }


        analysis.execute(params, dataset);
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
                    System.out.println("preprocessing finished.");
                    System.out.println("output dataset " + output.getClass().toString() + " name: " + output.getName() + ", size = " + output.size());
                    transform = output;

                    preprocessingFinished = true;
                    lock.notifyAll();
                }
            }
        });
        taskAnalyze.schedule(0);
    }
}
