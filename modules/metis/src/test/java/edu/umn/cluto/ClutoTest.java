/*
 * Copyright (C) 2011-2015 clueminer.org
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
package edu.umn.cluto;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import org.clueminer.utils.SystemInfo;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ClutoTest {

    private final Cluto subject;

    public ClutoTest() {
        subject = new Cluto();
    }

    @Test
    public void testCluster() {
        Dataset<? extends Instance> dataset = FakeDatasets.schoolData();
        Props params = new Props();
        params.put("k", 3);
        assertEquals(17, dataset.size());
        if (subject.getBinary().exists() && SystemInfo.isLinux()) {
            Clustering clustering = subject.cluster(dataset, params);
            assertEquals(2, clustering.size());
            assertEquals(dataset.size(), clustering.instancesCount());
        }
    }

    @Test
    public void testGlass() {
        Dataset<? extends Instance> dataset = FakeDatasets.glassDataset();
        Props params = new Props();
        params.put("k", 9);
        assertEquals(214, dataset.size());
        if (SystemInfo.isLinux()) {
            Clustering clustering = subject.cluster(dataset, params);
            assertEquals(9, clustering.size());
            assertEquals(dataset.size(), clustering.instancesCount());
        }
    }

}
