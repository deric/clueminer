package org.clueminer.hts.fluorescence;

import org.clueminer.transform.DatasetTransformation;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Task;

/**
 *
 * @author Tomas Barton
 */
public class AnalyzeRunner extends Task implements Runnable {

    private Timeseries<ContinuousInstance> dataset;
    private Dataset<Instance> output;
    private ProgressHandle p;

    public AnalyzeRunner(Timeseries<ContinuousInstance> dataset, Dataset<Instance> output, ProgressHandle p) {
        this.dataset = dataset;
        this.output = output;
        this.p = p;
    }

    @Override
    public void run() {
        DatasetTransformation trans = new DatasetTransformation();
        trans.analyze(dataset, output, p); //for debugging can save results to CSV file
    }
    
    public Dataset<Instance> getAnalyzedData(){
        return output;
    }
}