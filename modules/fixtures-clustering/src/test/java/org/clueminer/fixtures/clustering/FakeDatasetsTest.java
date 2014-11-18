package org.clueminer.fixtures.clustering;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author deric
 */
public class FakeDatasetsTest {

    @Test
    public void testSchoolData() {
        Dataset<? extends Instance> school = FakeDatasets.schoolData();
        assertEquals(17, school.size());
        assertEquals(4, school.attributeCount());
        Instance inst = school.get(0);
        assertEquals(0, inst.getIndex());
        assertEquals("lau", inst.classValue());
    }

    @Test
    public void testGlassDataset() {
        Dataset<? extends Instance> glass = FakeDatasets.glassDataset();
        assertEquals(214, glass.size());
        assertEquals(9, glass.attributeCount());
        //TODO remove quotes from arff
        assertEquals("'build wind float'", glass.get(0).classValue());
    }

    @Test
    public void testIrisDataset() {
        Dataset<? extends Instance> iris = FakeDatasets.irisDataset();
        assertEquals(150, iris.size());
        assertEquals(4, iris.attributeCount());
    }

    @Test
    public void testUsArrestData() {
        Dataset<? extends Instance> usArrests = FakeDatasets.usArrestData();
        assertEquals(50, usArrests.size());
        assertEquals(4, usArrests.attributeCount());
        assertEquals("Alabama", usArrests.get(0).classValue());
        assertEquals("Wyoming", usArrests.get(49).classValue());
    }

}
