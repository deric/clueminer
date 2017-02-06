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
package org.clueminer.transform;

import java.io.IOException;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.impl.AttrHashDataset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author deric
 */
public class DatasetTransformationTest extends TsTest {

    protected static DatasetTransformation subject;
    protected Timeseries<ContinuousInstance> simple;

    public DatasetTransformationTest() throws IOException {
        subject = new DatasetTransformation();
        simple = loadData01();
    }

    @Test
    public void testAnalyzeTimeseries() {
        ProgressHandle ph = ProgressHandle.createHandle("Trasforming dataset");
        int degree = 24;
        Dataset<Instance> output = new AttrHashDataset<>(10, degree);
        //analyze data
        ph.start(degree * simple.size());
        subject.analyze(simple, output, ph);

        double sum = 0.0;
        for (int i = 0; i < output.attributeCount(); i++) {
            //check that all attributes were assigned some value
            sum += output.get(0, i);
        }
        //it's quite unlikely that the sum would be 0
        assertNotEquals(0.0, sum);
        for (int i = 0; i < output.size(); i++) {
            System.out.println(i + ": " + output.get(i));
        }
        assertEquals(1, output.size());
        ph.finish();
    }

}
