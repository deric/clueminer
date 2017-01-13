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
package org.clueminer.fixtures.clustering;

import org.clueminer.clustering.api.Clustering;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class FakeClusteringTest {

    public FakeClusteringTest() {
    }

    @Test
    public void testIris() {
        Clustering clust = FakeClustering.iris();
        assertEquals(3, clust.size());
        assertEquals(150, clust.instancesCount());
    }

    @Test
    public void testIrisWrong() {
        Clustering clust = FakeClustering.irisWrong();
        assertEquals(3, clust.size());
        assertEquals(150, clust.instancesCount());
    }

    @Test
    public void testIrisWrong2() {
        Clustering clust = FakeClustering.irisWrong2();
        assertEquals(3, clust.size());
        assertEquals(150, clust.instancesCount());
    }

    @Test
    public void testIrisWrong4() {
        Clustering clust = FakeClustering.irisWrong4();
        assertEquals(4, clust.size());
        assertEquals(150, clust.instancesCount());
    }

    @Test
    public void testIrisWrong5() {
        Clustering clust = FakeClustering.irisWrong5();
        assertEquals(5, clust.size());
        assertEquals(150, clust.instancesCount());
    }

    @Test
    public void testWine() {

    }

    @Test
    public void testWineCorrect() {
        Clustering clust = FakeClustering.wineCorrect();
        assertEquals(3, clust.size());
        assertEquals(27, clust.instancesCount());
    }

    @Test
    public void testWineClustering() {
        Clustering clust = FakeClustering.wineClustering();
        assertEquals(3, clust.size());
        assertEquals(27, clust.instancesCount());
    }

    @Test
    public void testExt100p2() {
        Clustering clust = FakeClustering.ext100p2();

        assertEquals(2, clust.size());
        assertEquals(100, clust.instancesCount());
    }

    @Test
    public void testExt100p3() {
        Clustering clust = FakeClustering.ext100p3();

        assertEquals(3, clust.size());
        assertEquals(100, clust.instancesCount());
    }

    @Test
    public void testIrisMostlyWrong() {
    }

    @Test
    public void testInt100p4() {
        Clustering clust = FakeClustering.int100p4();

        assertEquals(4, clust.size());
        assertEquals(400, clust.instancesCount());
        assertEquals(100, clust.get(0).size());
        assertEquals(100, clust.get(1).size());
        assertEquals(2, clust.get(2).attributeCount());
        assertEquals(2, clust.get(1).attributeCount());

        assertEquals(2, clust.instance(0).size());
        assertEquals(2, clust.instance(1).size());
        assertEquals(2, clust.instance(2).size());
    }

    @Test
    public void testSpirals() {
        Clustering clust = FakeClustering.spirals();
        assertNotNull(clust);
        assertEquals(2, clust.size());
        assertEquals(500, clust.get(0).size());
        assertEquals(500, clust.get(1).size());
    }

    @Test
    public void testKumar() {
        Clustering clust = FakeClustering.kumar();
        assertNotNull(clust);
        assertEquals(2, clust.size());
        assertEquals(3, clust.get(0).size());
        assertEquals(3, clust.get(1).size());
        assertEquals(6, clust.instancesCount());
    }

}
