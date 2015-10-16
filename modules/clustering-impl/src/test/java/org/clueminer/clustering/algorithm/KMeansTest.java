package org.clueminer.clustering.algorithm;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.dataset.row.SparseInstance;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.openide.util.Exceptions;

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
     * This is perhaps the best known database to be found in the pattern
     * recognition literature. Fisher's paper is a classic in the field and is
     * referenced frequently to this day. (See Duda & Hart, for example.) The
     * data set contains 3 classes of 50 instances each, where each class refers
     * to a type of iris plant. One class is linearly separable from the other
     * 2; the latter are NOT linearly separable from each other.
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

    /**
     * Test endless loop
     *
     * @TODO: make a thread safe implementation
     */
    //@Test
    public void testEndless() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                SparseInstance i1 = new SparseInstance(2);
                SparseInstance i2 = new SparseInstance(2);
                i1.put(1d);
                i2.put(2d);
                System.out.println("i1 size: " + i1.size());
                //current implementation is not thread safe!!!
                //however this test sometimes passes
                Dataset dataset = new SampleDataset(2);
                dataset.attributeBuilder().create("a", "NUMERIC");
                dataset.attributeBuilder().create("b", "NUMERIC");
                dataset.add(i1);
                dataset.add(i2);
                KMeans cluster = new KMeans();
                Props p = new Props();
                p.putInt("k", 2);
                p.putInt("iterations", 1);
                cluster.cluster(dataset, p);
            }
        });
        t.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            Exceptions.printStackTrace(e);
        }
        /*
         * If it is still alive, it is endlessly looping
         */
        assertFalse(t.isAlive());

    }
}
