/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.eval;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.io.arff.ARFFHandler;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class DunnIndexTest {

    private static final DunnIndex<Instance, Cluster<Instance>> subject = new DunnIndex<>(new EuclideanDistance());
    private static Cluster<Instance> cluster;
    private static final CommonFixture tf = new CommonFixture();
    private static final double delta = 1e-9;

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

    /**
     * Test of getName method, of class DunnIndex.
     */
    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    /**
     * Test of score method, of class DunnIndex.
     *
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws org.clueminer.exception.ParserError
     */
    @Test
    public void testScore() throws IOException, FileNotFoundException, ParserError, ScoreException {
        ClusteringAlgorithm km = new KMeans();
        ARFFHandler arff = new ARFFHandler();
        Dataset<Instance> iris = new ArrayDataset(150, 4);
        arff.load(tf.irisArff(), iris, 4);
        Props p = new Props();
        p.putInt("k", 3);
        Clustering clusters = km.cluster(iris, p);
        System.out.println("dunn=" + subject.score(clusters));

    }

    /**
     * Test of maxIntraClusterDistance method, of class DunnIndex.
     */
    @Test
    public void testMaxIntraClusterDistance() {
        double dist = subject.maxIntraClusterDistance(cluster);
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
        Cluster x = shuffle(cluster);
        dist = subject.maxIntraClusterDistance(x);
        assertEquals(Math.sqrt(50), dist, 0.0001);
    }

    public static Cluster<Instance> shuffle(Cluster<Instance> input) {
        Cluster<Instance> out = (Cluster<Instance>) input.copy();
        Random rnd = new Random();
        // Shuffle array
        for (int i = input.size(); i > 1; i--) {
            swap(out, i - 1, rnd.nextInt(i));
        }
        return out;
    }

    private static void swap(Cluster<Instance> out, int i, int j) {
        Instance tmp = out.get(i);
        out.set(i, out.get(j));
        out.set(j, tmp);
    }

    /**
     * Test of isBetter method, of class DunnIndex.
     */
    @Test
    public void testCompareScore() throws ScoreException {
        double scoreBetter = subject.score(FakeClustering.iris());
        double scoreWorser = subject.score(FakeClustering.irisMostlyWrong());

        //should recognize better clustering
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
    }

    /**
     * Check against definition (and tests in R package clusterCrit)
     * https://cran.r-project.org/web/packages/clusterCrit/index.html
     *
     * NOTE: There's a small problem with precision of floating point
     * operations. First 7 decimal digits seems to match.
     */
    @Test
    public void testClusterCrit() throws ScoreException {
        double score = subject.score(FakeClustering.int100p4());
        assertEquals(0.835485600869118, score, delta);
    }
}
