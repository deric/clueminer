package org.clueminer.clustering.algorithm;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class KMeansTest {

    private final KMeans subject;

    public KMeansTest() {
        subject = new KMeans();
    }

    /**
     * Test of cluster method, of class KMeans.
     */
    @Test
    public void testCluster() {
        Dataset<? extends Instance> dataset = FakeClustering.irisDataset();
        Props params = new Props();
        params.putInt(KMeans.K, 3);
        Clustering clustering = subject.cluster(dataset, params);
        assertEquals(3, clustering.size());
        assertEquals(dataset.size(), clustering.instancesCount());
    }
}