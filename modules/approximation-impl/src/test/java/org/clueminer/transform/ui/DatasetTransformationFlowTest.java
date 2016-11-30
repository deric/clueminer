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

import org.clueminer.fixtures.CommonFixture;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DatasetTransformationFlowTest {

    private DatasetTransformationFlow subject;
    private final CommonFixture CF = new CommonFixture();

    public DatasetTransformationFlowTest() {
        subject = new DatasetTransformationFlow();
    }

    @Test
    public void testGetInputs() {
        assertEquals(1, subject.getInputs().length);
    }

    @Test
    public void testGetOutputs() {
        assertEquals(1, subject.getOutputs().length);
    }

    //@Test
    public void testExecute() {
        Object[] in = new Object[1];
        in[0] = FakeDatasets.irisDataset();
        Props params = new Props();
        params.put("transformation", "curve parameters");
        Object[] out = subject.execute(in, params);
        assertEquals(1, out.length);
    }

}
