package org.clueminer.evaluation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.clueminer.attributes.AttributeType;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class DunnIndexTest {

    private static DunnIndex test = new DunnIndex();
    private static Dataset dataset;
    private static CommonFixture tf = new CommonFixture();

    public DunnIndexTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        dataset = new SampleDataset();
        dataset.setAttribute(0, dataset.attributeBuilder().create("x", AttributeType.NUMERICAL));
        dataset.setAttribute(0, dataset.attributeBuilder().create("y", AttributeType.NUMERICAL));
        ARFFHandler arff = new ARFFHandler();
        assertTrue(arff.load(tf.simpleCluster(), dataset, 2));
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getName method, of class DunnIndex.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of setDistanceMeasure method, of class DunnIndex.
     */
    @Test
    public void testSetDistanceMeasure() {
    }

    /**
     * Test of score method, of class DunnIndex.
     */
    @Test
    public void testScore() throws IOException, FileNotFoundException {
        ClusteringAlgorithm km = new KMeans(3, 100, new EuclideanDistance());
        ARFFHandler arff = new ARFFHandler();
        Dataset<Instance> iris = new SampleDataset();
        arff.load(tf.irisArff(), iris, 4);
        Clustering clusters = km.partition(iris);
        System.out.println("dunn="+test.score(clusters, iris));

    }

    /**
     * Test of maxIntraClusterDistance method, of class DunnIndex.
     */
    @Test
    public void testMaxIntraClusterDistance() {
        double dist = test.maxIntraClusterDistance(dataset);
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
        Collections.shuffle((ArrayList) dataset);
        dist = test.maxIntraClusterDistance(dataset);
        assertEquals(Math.sqrt(50), dist, 0.0001);

    }

    /**
     * Test of compareScore method, of class DunnIndex.
     */
    @Test
    public void testCompareScore() {
    }
}