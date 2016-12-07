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
package org.clueminer.transform;

import java.io.IOException;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.impl.AttrHashDataset;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author deric
 */
public class DatasetTransformationTest extends TsTest {

    private static DatasetTransformation subject;
    private Timeseries<ContinuousInstance> simple;

    public DatasetTransformationTest() throws IOException {
        subject = new DatasetTransformation();
        simple = loadData01();
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testAnalyzeTimeseries() {
        ProgressHandle ph = ProgressHandle.createHandle("Trasforming dataset");
        int degree = 24;
        Dataset<Instance> output = new AttrHashDataset<>(10, degree);
        //analyze data
        ph.start(degree * simple.size());
        subject.analyze(simple, output, ph);
        assertEquals(1, output.size());
        for (int i = 0; i < output.attributeCount(); i++) {
            //check that all attributes were assigned some value
            //System.out.println("attr [" + i + "] = " + output.get(0, i));
            assertEquals(true, output.get(0, i) != 0.0);
        }
        ph.finish();
    }

}
