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
package org.clueminer.clustering;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.report.MemInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class StdStorageTest {

    private StdStorage subject;
    private final MemInfo info;

    public StdStorageTest() {
        subject = new StdStorage(FakeClustering.irisDataset());
        info = new MemInfo();
    }

    @Test
    public void testGet() {
        info.startClock();
        Dataset<? extends Instance> data = subject.get("Min-Max", true);
        assertNotNull(data);
        assertEquals(150, data.size());
        info.stopClock();
        info.report();
    }

    @Test
    public void testIsCached() {
        //test dataset configuration which we haven't tried yet
        assertEquals(false, subject.isCached("Min-Max", false));
    }

    /*    @Test
     public void testGc() {
     }
     */
    @Test
    public void testGetDataset() {
    }

}
