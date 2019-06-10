/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.eval.utils;

import com.google.common.collect.Table;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.eval.external.ExternalTest;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.io.arff.ARFFHandler;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CountingPairsTest extends ExternalTest {

    private static Clustering clusters;
    private static final CommonFixture tf = new CommonFixture();
    private static Clustering iris;

    public CountingPairsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws FileNotFoundException, IOException, ParserError {
        clusters = FakeClustering.iris();

        //now try some real clustering
        ClusteringAlgorithm km = new KMeans();
        Props p = new Props();
        p.putInt("k", 3);
        ARFFHandler arff = new ARFFHandler();
        Dataset<Instance> irisDataset = new ArrayDataset(150, 4);
        arff.load(tf.irisArff(), irisDataset, 4);
        iris = km.cluster(irisDataset, p);
    }

    /**
     * Test of contingencyTable method, of class CountingPairs.
     */
    @Test
    public void testCountPairs() {
        //this clustering has all assignments correct
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        for (String row : table.rowKeySet()) {
            Map<String, Integer> map = table.row(row);
            for (String klass : map.keySet()) {
                int cnt = map.get(klass);
                //each class has 50 members
                assertEquals(50, cnt);
            }
        }
        System.out.println(table.toString());
    }

    /**
     * Test of findMatching method, of class CountingPairs.
     */
    @Test
    public void testFindMatching() {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(iris);
        Matching matching = CountingPairs.findMatching(table);
        //we have 3 different classes
        assertEquals(3, matching.size());

        //let's test stupid clustering
        // Dataset<Instance> irisData = FakeClustering.irisDataset();
        Clustering<Instance, Cluster<Instance>> irisClusters = FakeClustering.irisWrong();

        table = CountingPairs.contingencyTable(irisClusters);
        matching = CountingPairs.findMatching(table);
        //we have 3 different classes
        assertEquals(3, matching.size());
        System.out.println("table: " + table);
        System.out.println("matching: " + matching);
        //Iris-setosa will be in all 3 clusters
        //cluster 3 should be mapped to Iris-versicolor
        System.out.println("versicolor: " + matching.get("Iris-versicolor"));
        assertNotNull(matching.get("Iris-versicolor"));
    }

    @Test
    public void testEmptyClass() {
        System.out.println("===test empty class");
        Clustering<Instance, Cluster<Instance>> irisClusters = FakeClustering.irisWrong2();

        Table<String, String, Integer> table = CountingPairs.contingencyTable(irisClusters);
        Matching matching = CountingPairs.findMatching(table);

        System.out.println("table: " + table);
        System.out.println("matching: " + matching);
        //only 2 classes could be paired, third one is paired in a random way
        assertEquals(3, matching.size());
        //Iris-setosa will be in all 3 clusters
        //cluster 3 should be mapped to Iris-versicolor
        System.out.println("versicolor: " + matching.get("Iris-versicolor"));

        Map<String, Integer> res;

        for (String cluster : matching.values()) {
            res = CountingPairs.countAssignments(table, matching.getByCluster(cluster), cluster);
            System.out.println("wrong table: " + res);
        }

        //assertNotNull(matching.get("Iris-versicolor"));
    }

    /**
     * Test of countAssignments method, of class CountingPairs.
     */
    @Test
    public void testCountAssignments() {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(iris);
        Matching matching = CountingPairs.findMatching(table);
        Map<String, Integer> res;

        int tp, fp, fn, tn, sum;
        for (String cluster : matching.values()) {
            //System.out.println(cluster + " corresponds to " + matching.inverse().get(cluster));
            res = CountingPairs.countAssignments(table, matching.getByCluster(cluster), cluster);
            assertEquals(4, res.size());
            tp = res.get("tp");
            fp = res.get("fp");
            fn = res.get("fn");
            tn = res.get("tn");
            System.out.println("tp = " + tp + " | fp = " + fp);
            System.out.println("fn = " + fn + " | tn = " + tn);
            sum = tp + fp + fn + tn;
            assertEquals(iris.instancesCount(), sum);
        }

    }

    @Test
    public void testCountPairs2() {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(FakeClustering.irisMostlyWrong());
        Matching matching = CountingPairs.findMatching(table);
        //just 2 clusters, so 2 classes must belong to 1 cluster
        assertEquals(3, matching.size());
    }

    @Test
    public void testCountPairs3() {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(FakeClustering.irisWrong4());
        Matching matching = CountingPairs.findMatching(table);
        System.out.println("iris wrong4:");
        System.out.println("table: " + table);
        System.out.println("matching: " + matching);
        //only 2 classes could be paired, third one is paired in a random way
        assertEquals(3, matching.size());
    }

    @Test
    public void testMatchPairs_Clustering() throws ScoreException {
        Clustering<Instance, Cluster<Instance>> clust = pcaData();

        assertEquals(10, clust.instancesCount());
        assertEquals(2, clust.get(0).size());
        assertEquals(3, clust.get(1).size());
        assertEquals(5, clust.get(2).size());

        PairMatch pm = CountingPairs.getInstance().matchPairs(clust);
        assertEquals(7, pm.tp, DELTA);
        assertEquals(6, pm.fp, DELTA);
        assertEquals(7, pm.fn, DELTA);
        assertEquals(25, pm.tn, DELTA);
    }

    /**
     * Matching 2 clusterings of 100 items which are assigned to 2, respectively
     * 3 clusters
     */
    @Test
    public void testMatchPairsExtPartitioning() throws ScoreException {
        Clustering c1 = FakeClustering.ext100p2();
        assertEquals(100, c1.instancesCount());
        Clustering c2 = FakeClustering.ext100p3();
        assertEquals(100, c2.instancesCount());
        PairMatch pm = CountingPairs.getInstance().matchPairs(c1, c2);

        assertEquals(845, pm.tp, DELTA);
        assertEquals(806, pm.fp, DELTA);
        assertEquals(1609, pm.fn, DELTA);
        assertEquals(1690, pm.tn, DELTA);
        System.out.println(pm.toString());

        System.out.println("sum:" + pm.sum());

        //number of pairs in clustering
        assertEquals(CombinatoricsUtils.binomialCoefficient(100, 2), pm.sum(), DELTA);
        assertEquals(100 * (100 - 1) / 2.0, pm.sum(), DELTA);

    }

}
