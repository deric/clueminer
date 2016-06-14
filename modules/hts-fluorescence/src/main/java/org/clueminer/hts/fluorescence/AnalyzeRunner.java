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
    private DataTransform transform;

    public AnalyzeRunner(Timeseries<ContinuousInstance> dataset, Dataset<Instance> output, DataTransform transform, ProgressHandle p) {
        this.dataset = dataset;
        this.output = output;
        this.p = p;
        this.transform = transform;
    }

    @Override
    public void run() {
        transform.analyze(dataset, output, p); //for debugging can save results to CSV file
    }

    public Dataset<Instance> getAnalyzedData() {
        return output;
    }
}
