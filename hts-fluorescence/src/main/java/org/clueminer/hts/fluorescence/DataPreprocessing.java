package org.clueminer.hts.fluorescence;

import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.dendrogram.DendrogramTopComponent;
import org.clueminer.project.ProjectControllerImpl;
import org.clueminer.project.api.Workspace;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Tomas Barton
 */
public class DataPreprocessing implements TaskListener {

    private Dataset<? extends Instance> plate;
    private Dataset<Instance> output;
    private DendrogramTopComponent dendrogram;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);

    public DataPreprocessing(Dataset<? extends Instance> plate, DendrogramTopComponent dendrogram) {
        this.plate = plate;
        this.dendrogram = dendrogram;
    }

    public void start() {
        //analyze data
        ProgressHandle ph = ProgressHandleFactory.createHandle("Analyzing dataset");
        Timeseries<ContinuousInstance> dataset = (Timeseries<ContinuousInstance>) plate;
        output = new SampleDataset<Instance>();
        output.setParent((Dataset<Instance>) plate);

        final RequestProcessor.Task taskAnalyze = RP.create(new AnalyzeRunner(dataset, output, ph));
        taskAnalyze.addTaskListener(this);
        taskAnalyze.schedule(0);
    }

    @Override
    public void taskFinished(Task task) {
        ProjectControllerImpl pc = Lookup.getDefault().lookup(ProjectControllerImpl.class);
        Workspace workspace = pc.getCurrentWorkspace();
        if (workspace != null) {
            System.out.println("workspace: " + workspace.toString());
            System.out.println("adding preprocessed plate to lookup");
            workspace.add(output);  //add plate to project's lookup
        }
        dendrogram.setPreprocessedDataset(output);
    }
}
