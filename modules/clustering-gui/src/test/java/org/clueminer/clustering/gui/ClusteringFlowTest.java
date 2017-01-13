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
package org.clueminer.clustering.gui;

import java.io.IOException;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ClusteringFlowTest {

    private final ClusteringFlow subject;

    public ClusteringFlowTest() {
        subject = new ClusteringFlow();
    }

    @Test
    public void testGetInputs() {
        assertEquals(1, subject.getInputs().length);
    }

    @Test
    public void testGetOutputs() {
        assertEquals(2, subject.getOutputs().length);
    }

    @Test
    public void testExecute() throws IOException {
        Object[] in = new Object[1];
        in[0] = FakeDatasets.irisDataset();
        Props params = new Props();
        params.put(AlgParams.ALG, "HC-LW");
        Object[] out = subject.execute(in, params);
        assertEquals(2, out.length);
        assertTrue(out[0] instanceof Clustering);
        /* Dataset d = (Dataset) out[0];
           assertEquals(1, d.size());
        assertEquals(9, d.attributeCount()); */
    }


}
