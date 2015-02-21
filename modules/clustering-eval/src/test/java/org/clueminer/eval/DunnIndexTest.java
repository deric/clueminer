package org.clueminer.eval;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.PartitioningClustering;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.clueminer.utils.DatasetTools;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class DunnIndexTest {

    private static final DunnIndex test = new DunnIndex(new EuclideanDistance());
    private static Cluster<? extends Instance> cluster;
    private static final CommonFixture tf = new CommonFixture();

    public DunnIndexTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        cluster = new BaseCluster(10, 2);
        cluster.setAttribute(0, cluster.attributeBuilder().create("x", BasicAttrType.NUMERICAL));
        cluster.setAttribute(0, cluster.attributeBuilder().create("y", BasicAttrType.NUMERICAL));
        ARFFHandler arff = new ARFFHandler();
        assertTrue(arff.load(tf.simpleCluster(), cluster, 2));
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getName method, of class DunnIndex.
     */
    @Test
    public void testGetName() {
        assertNotNull(test.getName());
    }

    /**
     * Test of setDistanceMeasure method, of class DunnIndex.
     */
    @Test
    public void testSetDistanceMeasure() {
    }

    /**
     * Test of score method, of class DunnIndex.
     *
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testScore() throws IOException, FileNotFoundException {
        PartitioningClustering km = new KMeans(3, 100, new EuclideanDistance());
        ARFFHandler arff = new ARFFHandler();
        Dataset<Instance> iris = new SampleDataset();
        arff.load(tf.irisArff(), iris, 4);
        Clustering clusters = km.partition(iris);
        System.out.println("dunn=" + test.score(clusters, iris));

    }

    /**
     * Test of maxIntraClusterDistance method, of class DunnIndex.
     */
    @Test
    public void testMaxIntraClusterDistance() {
        double dist = test.maxIntraClusterDistance(cluster);
        System.out.println("clus" + cluster.toString());
        /*
         * max distance in dataset is between points [-3,-3] and [2, 2] which is
         * in Euclidean space d = sqrt((-3-2)^2+(-3-2)^2) = sqrt(25+25)
         */
        assertEquals(Math.sqrt(50), dist, 0.0001);
        System.out.println("max distance = " + dist);

        /*
         * after changing order of elements in dataset the distance should stay
         * the same
         */
        Dataset<? extends Instance> x = DatasetTools.shuffle(cluster);
        dist = test.maxIntraClusterDistance(x);
        System.out.println("shuffeled: " + x.toString());

//        assertEquals(Math.sqrt(50), dist, 0.0001);
    }

    /**
     * Test of isBetter method, of class DunnIndex.
     */
    @Test
    public void testCompareScore() {
    }
}
