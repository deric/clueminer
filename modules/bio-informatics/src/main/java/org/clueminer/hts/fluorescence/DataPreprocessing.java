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
package org.clueminer.hts.fluorescence;

import org.clueminer.approximation.api.DataTransform;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.impl.SampleDataset;
import org.clueminer.project.impl.ProjectControllerImpl;
import org.clueminer.project.api.Workspace;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Tomas Barton
 */
public class DataPreprocessing<E extends Instance> implements TaskListener {

    private Dataset<E> plate;
    private Dataset<E> output;
    private DataTransform transform;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);

    public DataPreprocessing(Dataset<E> plate, DataTransform transform) {
        this.plate = plate;
        this.transform = transform;
    }

    public void start() {
        //analyze data
        ProgressHandle ph = ProgressHandle.createHandle("Analyzing dataset");
        Timeseries<ContinuousInstance> dataset = (Timeseries<ContinuousInstance>) plate;
        output = new SampleDataset<>();
        output.setParent(plate);

        AnalyzeRunner<Timeseries<ContinuousInstance>, Dataset<E>> ar = new AnalyzeRunner(transform, ph);
        ar.setup(dataset, output);
        final RequestProcessor.Task taskAnalyze = RP.create(ar);
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
        plate.addChild(transform.getName(), output);
    }
}
