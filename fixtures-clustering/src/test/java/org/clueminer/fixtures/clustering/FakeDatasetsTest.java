package org.clueminer.fixtures.clustering;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author deric
 */
public class FakeDatasetsTest {

    public FakeDatasetsTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSchoolData() {
        Dataset<? extends Instance> school = FakeDatasets.schoolData();
        assertEquals(17, school.size());
        assertEquals(4, school.attributeCount());
    }

    @Test
    public void testGlassDataset() {
        Dataset<? extends Instance> glass = FakeDatasets.glassDataset();
        assertEquals(214, glass.size());
        assertEquals(9, glass.attributeCount());
    }

}
