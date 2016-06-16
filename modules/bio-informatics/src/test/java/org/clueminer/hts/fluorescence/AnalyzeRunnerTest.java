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

import java.io.IOException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.SampleDataset;
import org.clueminer.fixtures.BioFixture;
import org.junit.Test;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class AnalyzeRunnerTest {

    public AnalyzeRunnerTest() {
    }

    @Test
    public void testRun() {
        System.out.println("run");
        FluorescenceImporter importer = null;
        try {
            BioFixture tf = new BioFixture();
            importer = new FluorescenceImporter(tf.apData());
            ProgressHandle ph = ProgressHandle.createHandle("Importing dataset");
            importer.setProgressHandle(ph);

            importer.run();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        //FluorescenceDataset dataset = importer.getDataset();
        Dataset<? extends Instance> plate = importer.getDataset();
        //System.out.println("inst A1 "+ plate.instance(0).toString());
        //System.out.println("plate "+plate.toString());
        Dataset<Instance> output = new SampleDataset<Instance>();
        output.setParent((Dataset<Instance>) plate);
        ProgressHandle ph = ProgressHandle.createHandle("Analyzing dataset");
        //   AnalyzeRunner instance = new AnalyzeRunner((Timeseries<ContinuousInstance>) plate, output, ph);
        //   instance.run();

    }

    @Test
    public void testGetAnalyzedData() {
        System.out.println("getAnalyzedData");

    }
}
