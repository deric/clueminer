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
package org.clueminer.transform.ui;

import java.io.IOException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.transform.TsTest;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DatasetTransformationFlowTest extends TsTest {

    private final DatasetTransformationFlow subject;

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

    @Test
    public void testExecute() throws IOException {
        Object[] in = new Object[1];
        in[0] = loadData01();
        Props params = new Props();
        params.put("transformation", "curve parameters");
        Object[] out = subject.execute(in, params);
        assertEquals(1, out.length);
        assertTrue(out[0] instanceof Dataset);
        Dataset d = (Dataset) out[0];
        assertEquals(1, d.size());
        assertEquals(9, d.attributeCount());
    }


}
