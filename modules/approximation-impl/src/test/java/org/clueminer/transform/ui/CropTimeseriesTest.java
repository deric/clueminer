/*
 * Copyright (C) 2011-2018 clueminer.org
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
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.transform.TsTest;
import static org.clueminer.transform.TsTest.loadData01;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CropTimeseriesTest extends TsTest {

    private final CropTimeseries subject;

    public CropTimeseriesTest() {
        subject = new CropTimeseries();
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
    public void testExecute() throws IOException {
        Object[] in = new Object[1];
        in[0] = loadData01();
        Props params = new Props();
        Object[] out = subject.execute(in, params);
        assertEquals(1, out.length);
        assertTrue(out[0] instanceof Timeseries);
        Timeseries d = (Timeseries) out[0];
        assertEquals(1, d.size());
        //no cropping at all
        assertEquals(15, d.attributeCount());
    }

    @Test
    public void testCropping() throws IOException {
        Object[] in = new Object[1];
        in[0] = loadData01();
        Props params = new Props();
        params.putDouble(CropTimeseries.CROP_START, 1.0);
        params.putDouble(CropTimeseries.CROP_END, 5.0);
        Object[] out = subject.execute(in, params);
        assertEquals(1, out.length);
        assertTrue(out[0] instanceof Timeseries);
        Timeseries d = (Timeseries) out[0];
        assertEquals(1, d.size());
        assertEquals(5, d.attributeCount());
    }

}
