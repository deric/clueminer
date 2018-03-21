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
package org.clueminer.eval;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class SilhouetteTest {

    private static final Silhouette subject = new Silhouette();
    private static Dataset<? extends Instance> dataset;
    private static Clustering clustering;
    private static Clustering clusters;
    private static Props params;
    private static HierarchicalResult rowsResult;
    private static Matrix input;
    private static final double delta = 1e-9;

    public SilhouetteTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        clusters = FakeClustering.iris();
        dataset = FakeDatasets.irisDataset();

        params = getParams();
        input = new JMatrix(dataset.arrayCopy());

        /* ClusteringAlgorithm algorithm = new HCL();
         * algorithm.setDistanceFunction(new EuclideanDistance());
         * params.setProperty("method-linkage", String.valueOf(0)); //-1=single, 0=complete, 1/2=average
         * rowsResult = algorithm.hierarchy(input, dataset, params); */
        ///clustering = rowsResult.getClustering(dendroData.getRowsMapping(), dataset);
    }

    private static Props getParams() {
        Props p = new Props();
        // alg name
        p.put("name", "HCL");
        p.putDouble("distance-factor", 1.0);
        p.putDouble("hcl-distance-absolute", 1.0);

        p.putBoolean("calculate-experiments", true);
        p.putBoolean("optimize-rows-ordering", true);
        p.putBoolean("optimize-cols-ordering", true);
        p.putBoolean("optimize-sample-ordering", true);
        p.putBoolean("calculate-rows", true);
        return p;
    }

    /**
     * Test of getName method, of class Silhouette.
     */
    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    /**
     * Test of score method, of class Silhouette.
     */
    //@Test
    public void testScore_Clustering() throws ScoreException {
        double score;
        long start = System.currentTimeMillis();
        score = subject.score(clustering);
        System.out.println("Silhouette= " + score);
        long end = System.currentTimeMillis();
        assertTrue(score != Double.NaN);
        System.out.println("computing took = " + (end - start) + " ms");
    }

    @Test
    public void testScore_ClusteringMatlab() throws ScoreException {
        double score;
        long start = System.currentTimeMillis();
        score = subject.score(clusters);
        System.out.println("Silhouette= " + score);
        //distance must be computed without SQRT!
        //depends on iris dataset version
        double matlab = 0.6566670178786608;
        long end = System.currentTimeMillis();
        assertTrue(score != Double.NaN);
        assertEquals(matlab, score, delta);
        System.out.println("computing took = " + (end - start) + " ms");
    }

    @Test
    public void testScore_Clustering_Matrix() throws ScoreException {
        //clustering containing cluster with single instance
        double score = subject.score(FakeClustering.irisMostlyWrong());
        assertNotSame(Double.NaN, score);

        //distance must be computed without SQRT!
        double ref = 0.5180062598611462;
        assertEquals(ref, score, delta);

    }

    /**
     * Test of isBetter method, of class Silhouette.
     */
    @Test
    public void testCompareScore() {
        assertEquals(true, subject.isBetter(0.0, -1.0));
        assertEquals(true, subject.isBetter(1.0, 0.456));
    }

    @Test
    public void testClusterCrit() throws ScoreException {
        double score = subject.score(FakeClustering.int100p4());
        //clusterCrit score is 0.827206253180689 because of using sqrt on distances
        assertEquals(0.9612636717273991, score, delta);
    }
}
