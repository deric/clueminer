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
