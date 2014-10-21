package org.clueminer.fixtures.clustering;

import org.clueminer.clustering.api.Clustering;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class FakeClusteringTest {

    public FakeClusteringTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
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

}
