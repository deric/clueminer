package org.clueminer.cluster.algorithm;

import java.io.IOException;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.dataset.row.SparseInstance;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.FileHandler;
import org.clueminer.utils.Props;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author tom
 */
public class KMeansTest {

    public KMeansTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
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
    public void testIris() {
        try {
            /*
             * Load a dataset
             */
            CommonFixture tf = new CommonFixture();
            Dataset data = new SampleDataset();
            data.attributeBuilder().create("sepal length", BasicAttrType.NUMERICAL);
            data.attributeBuilder().create("sepal width", BasicAttrType.NUMERICAL);
            data.attributeBuilder().create("petal length", BasicAttrType.NUMERICAL);
            data.attributeBuilder().create("petal width", BasicAttrType.NUMERICAL);
            FileHandler.loadDataset(tf.irisData(), data, 4, ",");

            ClusteringAlgorithm km = new KMeans();
            Props p = new Props();
            p.putInt("k", 3);
            /*
             * Cluster the data, it will be returned as an array of data sets,
             * with each dataset representing a cluster
             */
            Clustering<? extends Cluster> clusters = km.cluster(data, p);
            System.out.println("Cluster count: " + clusters.size());
            int i = 0;
            for (Cluster d : clusters) {
                System.out.println("Dataset " + i++);
                for (Object o : d.getClasses()) {
                    System.out.println(o.toString());
                }

            }
            //we have 3 classes there, we do NOT expect k-means to be sucesseful
            //in separating this 3 classes
            assertEquals(3, clusters.size());
            //  ClusterEvaluation ci = new CIndex();
            // System.out.println("CIndex= " + ci.score(clusters, data));
            //  ClusterEvaluation gamma = new Gamma();
            // System.out.println("gamma= " + gamma.score(clusters, data));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }

    }

    /**
     * Test endless loop
     *
     * @TODO make a thread safe implementation
     */
    @Test
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
