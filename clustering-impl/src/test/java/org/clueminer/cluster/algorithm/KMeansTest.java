package org.clueminer.cluster.algorithm;

import java.io.IOException;
import org.clueminer.attributes.AttributeType;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.dataset.row.SparseInstance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.evaluation.CIndex;
import org.clueminer.evaluation.Gamma;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.FileHandler;
import org.junit.AfterClass;
import static org.junit.Assert.*;
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
    public void testIris() throws UnsupportedAttributeType {
        try {
            /*
             * Load a dataset
             */
            CommonFixture tf = new CommonFixture();
            Dataset data = new SampleDataset();
            data.setAttribute(0, data.attributeBuilder().create("sepal length", AttributeType.NUMERICAL));
            data.setAttribute(1, data.attributeBuilder().create("sepal width", AttributeType.NUMERICAL));
            data.setAttribute(2, data.attributeBuilder().create("petal length", AttributeType.NUMERICAL));
            data.setAttribute(3, data.attributeBuilder().create("petal width", AttributeType.NUMERICAL));
            FileHandler.loadDataset(tf.irisData(), data, 4, ",");

            ClusteringAlgorithm km = new KMeans(3, 100, new EuclideanDistance());
            /*
             * Cluster the data, it will be returned as an array of data sets,
             * with each dataset representing a cluster
             */
            Clustering<Cluster> clusters = km.partition(data);
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
            ClusterEvaluation ci = new CIndex();
            System.out.println("CIndex= " + ci.score(clusters, data));
            ClusterEvaluation gamma = new Gamma();
            System.out.println("gamma= " + gamma.score(clusters, data));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }

    }

    /**
     * Test endless loop
     * @TODO make a thread safe implementation
     */
    @Test
    public void testEndless() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    SparseInstance i1 = new SparseInstance(2);
                    SparseInstance i2 = new SparseInstance(2);
                    i1.put(1d);
                    i2.put(2d);
                    System.out.println("i1 size: " + i1.size());
                    //current implementation is not thread safe!!!
                    //however this test sometimes passes
                    Dataset dataset = new SampleDataset(2);
                    dataset.setAttribute(0, dataset.attributeBuilder().create("a", "NUMERIC"));
                    dataset.setAttribute(1, dataset.attributeBuilder().create("b", "NUMERIC"));
                    dataset.add(i1);
                    dataset.add(i2);
                    KMeans cluster = new KMeans(2, 1);
                    cluster.partition(dataset);
                } catch (UnsupportedAttributeType ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        t.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /*
         * If it is still alive, it is endlessly looping
         */
        assertFalse(t.isAlive());


    }
}
