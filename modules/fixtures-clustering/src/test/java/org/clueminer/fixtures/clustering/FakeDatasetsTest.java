package org.clueminer.fixtures.clustering;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

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

    @Test
    public void testVehicle() {
        Dataset<? extends Instance> vehicle = FakeDatasets.vehicleDataset();
        assertEquals(846, vehicle.size());
        assertEquals(18, vehicle.attributeCount());
        assertEquals("van", vehicle.get(0).classValue());
        assertEquals("saab", vehicle.get(49).classValue());
    }

    @Test
    public void testBlobs() {
        Dataset<? extends Instance> blobs = FakeDatasets.blobs();
        assertEquals(300, blobs.size());
        assertEquals(2, blobs.attributeCount());
        assertEquals("0", blobs.get(0).classValue());
        assertEquals("1", blobs.get(2).classValue());
    }

    @Test
    public void testGaussians1() {
        Dataset<? extends Instance> gaussians1 = FakeDatasets.gaussians1();
        assertEquals(100, gaussians1.size());
        assertEquals(2, gaussians1.attributeCount());
        assertEquals("1", gaussians1.get(0).classValue());
        assertEquals("1", gaussians1.get(1).classValue());
    }
}
