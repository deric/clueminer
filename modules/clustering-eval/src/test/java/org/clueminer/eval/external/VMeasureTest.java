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
package org.clueminer.eval.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.clueminer.eval.external.ExternalTest.DELTA;

/**
 *
 * @author deric
 */
public class VMeasureTest extends ExternalTest {

    private static Clustering irisCorrect;
    private static Clustering irisWrong;
    private static Dataset<? extends Instance> pmData;
    private static Dataset<? extends Instance> pmData2;

    public VMeasureTest() throws FileNotFoundException, IOException {
        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong2();
        subject = new VMeasure();
    }

    @BeforeClass
    public static void setUpClass() {
        /**
         * Dataset from V-Measure paper
         */
        pmData = new ArrayDataset(15, 1);
        for (int i = 0; i < 15; i++) {
            if (i < 5) {
                pmData.builder().create(new double[]{1.0}, "square");
            } else if (i < 10) {
                pmData.builder().create(new double[]{1.0}, "star");
            } else {
                pmData.builder().create(new double[]{1.0}, "circle");
            }
        }

        pmData2 = new ArrayDataset(21, 1);
        for (int i = 0; i < 21; i++) {
            if (i < 7) {
                pmData2.builder().create(new double[]{1.0}, "square");
            } else if (i < 14) {
                pmData2.builder().create(new double[]{1.0}, "star");
            } else {
                pmData2.builder().create(new double[]{1.0}, "circle");
            }
        }
    }

    @Test
    public void testScore_Clustering_Clustering() throws ScoreException {
        //this is fixed clustering which correspods to true classes in dataset
        measure(FakeClustering.iris(), FakeClustering.iris(), 1.0);

        double score = measure(irisWrong, irisCorrect, 0.6496820278112178);

        double score2 = measure(FakeClustering.irisWrong(), irisCorrect, 0.06793702240876041);
        assertTrue(score2 < score);
    }

    @Test
    public void testScore_Clustering_Dataset() throws ScoreException {
        measure(FakeClustering.iris(), 1.0);

        double score = measure(irisWrong, 0.6496820278112178);
        double score2 = measure(FakeClustering.irisWrong(), irisCorrect, 0.06793702240876041);

        assertTrue(score2 < score);
    }

    @Test
    public void testCompareScore() {
        //one is better than zero
        assertTrue(subject.isBetter(1.0, 0.0));
        assertTrue(subject.isBetter(1.0, 0.5));
        assertTrue(subject.isBetter(1.0, 0.9999));
    }

    @Test
    public void testScore() throws ScoreException {
        Clustering c = new ClusterList(2);
        Dataset<? extends Instance> d = new ArrayDataset(8, 2);
        d.builder().create(new double[]{0, 0}, "0");
        d.builder().create(new double[]{0, 0}, "0");
        d.builder().create(new double[]{0, 0}, "0");
        d.builder().create(new double[]{1, 1}, "1");
        d.builder().create(new double[]{1, 1}, "0");
        d.builder().create(new double[]{1, 1}, "1");
        d.builder().create(new double[]{1, 1}, "1");
        d.builder().create(new double[]{1, 1}, "1");
        assertEquals(8, d.size());
        Cluster a = c.createCluster(0, 4);
        Cluster b = c.createCluster(1, 4);
        for (int i = 0; i < 4; i++) {
            a.add(d.get(i));
            b.add(d.get(i + 4));
        }
        assertEquals(2, c.size());
        assertEquals(0.18872187554086717, subject.score(c), DELTA);
    }

    @Test
    public void testCorrectSolution() throws ScoreException {
        Clustering correct = new ClusterList(3);

        assertEquals(15, pmData.size());
        Cluster s1 = correct.createCluster(0, 5);
        Cluster s2 = correct.createCluster(1, 5);
        Cluster s3 = correct.createCluster(2, 5);
        for (int i = 0; i < 5; i++) {
            s1.add(pmData.get(i));
            s2.add(pmData.get(i + 5));
            s3.add(pmData.get(i + 10));
        }
        assertEquals(3, correct.size());
        assertEquals(1.0, subject.score(correct), DELTA);
    }

    /**
     * Based on examples of the Problem of Matching from original paper (Figure 2a)
     */
    @Test
    public void testSolutionA() throws ScoreException {
        Clustering a = new ClusterList(3);

        assertEquals(15, pmData.size());
        Cluster s1 = a.createCluster(0, 5);
        Cluster s2 = a.createCluster(1, 5);
        Cluster s3 = a.createCluster(2, 5);

        //squares
        s1.add(pmData.get(0));
        s1.add(pmData.get(1));
        s1.add(pmData.get(2));
        s2.add(pmData.get(3));
        s3.add(pmData.get(4));
        //stars
        s1.add(pmData.get(5));
        s2.add(pmData.get(6));
        s2.add(pmData.get(7));
        s2.add(pmData.get(8));
        s3.add(pmData.get(9));
        //circles
        s1.add(pmData.get(10));
        s2.add(pmData.get(11));
        s3.add(pmData.get(12));
        s3.add(pmData.get(13));
        s3.add(pmData.get(14));

        assertEquals(3, a.size());
        assertEquals(5, s1.size());
        assertEquals(5, s2.size());
        assertEquals(5, s3.size());
        assertEquals(0.13502647928207268, subject.score(a), DELTA);
    }

    /**
     * Based on examples of the Problem of Matching from original paper (Figure 2b)
     */
    @Test
    public void testSolutionB() throws ScoreException {
        Clustering a = new ClusterList(6);

        assertEquals(15, pmData.size());
        Cluster s1 = a.createCluster(0, 5);
        Cluster s2 = a.createCluster(1, 5);
        Cluster s3 = a.createCluster(2, 5);

        //squares
        s1.add(pmData.get(0));
        s1.add(pmData.get(1));
        s1.add(pmData.get(2));
        s2.add(pmData.get(3));
        s2.add(pmData.get(4));
        //stars
        s2.add(pmData.get(5));
        s2.add(pmData.get(6));
        s2.add(pmData.get(7));
        s3.add(pmData.get(8));
        s3.add(pmData.get(9));
        //circles
        s1.add(pmData.get(10));
        s1.add(pmData.get(11));
        s3.add(pmData.get(12));
        s3.add(pmData.get(13));
        s3.add(pmData.get(14));

        assertEquals(3, a.size());
        assertEquals(5, s1.size());
        assertEquals(5, s2.size());
        assertEquals(5, s3.size());
        assertEquals(0.3873983807106558, subject.score(a), DELTA);
    }

    /**
     * Based on examples of the Problem of Matching from original paper (Figure 2c)
     */
    @Test
    public void testSolutionC() throws ScoreException {
        Clustering a = new ClusterList(3);

        assertEquals(21, pmData2.size());
        Cluster s1 = a.createCluster(0, 5);
        Cluster s2 = a.createCluster(1, 5);
        Cluster s3 = a.createCluster(2, 5);
        Cluster s4 = a.createCluster(3, 2);
        Cluster s5 = a.createCluster(4, 2);
        Cluster s6 = a.createCluster(5, 2);

        //squares
        s1.add(pmData2.get(0));
        s1.add(pmData2.get(1));
        s1.add(pmData2.get(2));
        s2.add(pmData2.get(3));
        s2.add(pmData2.get(4));
        s4.add(pmData2.get(5));
        s5.add(pmData2.get(6));
        //stars
        s2.add(pmData2.get(7));
        s2.add(pmData2.get(8));
        s2.add(pmData2.get(9));
        s3.add(pmData2.get(10));
        s3.add(pmData2.get(11));
        s5.add(pmData2.get(12));
        s6.add(pmData2.get(13));
        //circles
        s1.add(pmData2.get(14));
        s1.add(pmData2.get(15));
        s3.add(pmData2.get(16));
        s3.add(pmData2.get(17));
        s3.add(pmData2.get(18));
        s4.add(pmData2.get(19));
        s6.add(pmData2.get(20));

        assertEquals(6, a.size());
        assertEquals(5, s1.size());
        assertEquals(5, s2.size());
        assertEquals(5, s3.size());
        assertEquals(2, s4.size());
        assertEquals(2, s5.size());
        assertEquals(2, s6.size());
        assertEquals(0.3003745261967547, subject.score(a), DELTA);
    }

    /**
     * Based on examples of the Problem of Matching from original paper (Figure 2d)
     */
    @Test
    public void testSolutionD() throws ScoreException {
        Clustering a = new ClusterList(3);

        assertEquals(21, pmData2.size());
        Cluster s1 = a.createCluster(0, 5);
        Cluster s2 = a.createCluster(1, 5);
        Cluster s3 = a.createCluster(2, 5);
        Cluster s4 = a.createCluster(3, 1);
        Cluster s5 = a.createCluster(4, 1);
        Cluster s6 = a.createCluster(5, 1);
        Cluster s7 = a.createCluster(6, 1);
        Cluster s8 = a.createCluster(7, 1);
        Cluster s9 = a.createCluster(8, 1);

        //squares
        s1.add(pmData2.get(0));
        s1.add(pmData2.get(1));
        s1.add(pmData2.get(2));
        s2.add(pmData2.get(3));
        s2.add(pmData2.get(4));
        s4.add(pmData2.get(5));
        s8.add(pmData2.get(6));
        //stars
        s2.add(pmData2.get(7));
        s2.add(pmData2.get(8));
        s2.add(pmData2.get(9));
        s3.add(pmData2.get(10));
        s3.add(pmData2.get(11));
        s5.add(pmData2.get(12));
        s9.add(pmData2.get(13));
        //circles
        s1.add(pmData2.get(14));
        s1.add(pmData2.get(15));
        s3.add(pmData2.get(16));
        s3.add(pmData2.get(17));
        s3.add(pmData2.get(18));
        s7.add(pmData2.get(19));
        s6.add(pmData2.get(20));

        assertEquals(9, a.size());
        assertEquals(5, s1.size());
        assertEquals(5, s2.size());
        assertEquals(5, s3.size());
        assertEquals(1, s4.size());
        assertEquals(1, s5.size());
        assertEquals(1, s6.size());
        assertEquals(1, s7.size());
        assertEquals(1, s8.size());
        assertEquals(1, s9.size());
        assertEquals(0.4128158969726665, subject.score(a), DELTA);
    }

}
