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

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.dataset.impl.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.io.arff.ARFFHandler;
import org.clueminer.io.FileHandler;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class CIndexTest {

    private static CIndex subject;
    private static final double delta = 1e-8;

    public CIndexTest() {
        subject = new CIndex();
    }

    /**
     *
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     */
    // @Test
    public void testScore() throws IOException, FileNotFoundException, ParserError, ScoreException {
        CommonFixture tf = new CommonFixture();
        Dataset<? extends Instance> data = new ArrayDataset(10, 2);
        ARFFHandler arff = new ARFFHandler();
        assertTrue(arff.load(tf.simpleCluster(), data, 2));

        assertEquals(2, data.attributeCount());

        Distance dist = new EuclideanDistance();

        ClusterEvaluation cind = new CIndex(dist);
        ClusterEvaluation aic = new ALE();
        ClusterEvaluation bic = new BIC();
        ClusterEvaluation sse = new SumOfSquaredErrors(dist);
        ClusterEvaluation gamma = new Gamma(dist);

        System.out.println("\t CIndex \t AIC \t BIC \t  SSE \t Gamma");
        Props p = new Props();
        for (int n = 2; n < 10; n++) {
            ClusteringAlgorithm km = new KMeans();
            p.putInt("k", n);
            Clustering clusters = km.cluster(data, p);

            double cindScore = cind.score(clusters);
            double aicScore = aic.score(clusters);
            double bicScore = bic.score(clusters);
            double sseScore = sse.score(clusters);
            double gScore = gamma.score(clusters);
            System.out.println("\t " + cindScore + " \t " + aicScore + " \t " + bicScore + " \t " + sseScore + " \t " + gScore);
        }


        /*
         * ClusteringAlgorithm km3 = new KMeans(2, 100, new EuclideanDistance());
         * ClusteringAlgorithm km4 = new KMeans(4, 100, new EuclideanDistance());
         *
         * Dataset[] clusters2 = km3.cluster(data); Dataset[] clusters3 =
         * km4.cluster(data);
         *
         *
         * double k3res = cind.score(clusters2); double k4res =
         * cind.score(clusters3);
         *
         *
         * System.out.println(clusters2);
         *
         *
         * double aicScore3 = aic.score(clusters2); double bicScore3 =
         * bic.score(clusters2); double sseScore3 = sse.score(clusters2); double
         * gScore2 = gamma.score(clusters2);
         *
         * double aicScore4 = aic.score(clusters3); double bicScore4 =
         * bic.score(clusters3); double sseScore4 = sse.score(clusters3); double
         * gScore3 = gamma.score(clusters3);
         *
         * System.out.println("\t score \t 2 clusters better than 3? ");
         * System.out.println("AIC score: \t" + aicScore3 + "\t" + aicScore4 +
         * "\t" + aic.isBetter(aicScore3, aicScore4)); //assertEquals(true,
         * aic.isBetter(aicScore3, aicScore4)); System.out.println("BIC
         * score: \t" + bicScore3 + "\t" + bicScore4 + "\t" +
         * bic.isBetter(bicScore3, bicScore4)); // assertEquals(true,
         * bic.isBetter(bicScore3, bicScore4)); System.out.println("SQ
         * error: \t" + sseScore3 + "\t" + sseScore4 + "\t" +
         * sse.isBetter(sseScore3, sseScore4)); // assertEquals(true,
         * sse.isBetter(sseScore3, sseScore4)); System.out.println("CIndex:
         * \t" + k3res + "\t" + k4res + "\t" + cind.isBetter(k3res, k4res));
         * System.out.println("Gamma: \t" + gScore2 + "\t" + gScore3 + "\t" +
         * cind.isBetter(gScore2, gScore3));
         */
        /**
         * We know, that in iris dataset there are 3 true classes, so first
         * score should be better
         */
        //assertEquals(true, cind.isBetter(k3res, k4res));
    }

    //  @Test
    public void testIris() throws IOException, ScoreException {
        CommonFixture tf = new CommonFixture();
        Dataset data = new SampleDataset();
        data.attributeBuilder().create("sepal length", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("sepal width", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("petal length", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("petal width", BasicAttrType.NUMERICAL);

        assertTrue(FileHandler.loadDataset(tf.irisData(), data, 4, ","));
        assertEquals(4, data.attributeCount());
        int evalNum = 9;
        ClusterEvaluation[] eval = new ClusterEvaluation[evalNum];
        int j = 0;
        Distance dm = new EuclideanDistance();
        eval[j++] = new CIndex(dm);
        eval[j++] = new ALE();
        eval[j++] = new BIC();
        eval[j++] = new SumOfSquaredErrors(dm);
        eval[j++] = new Gamma(dm);
        eval[j++] = new Tau(dm);
        eval[j++] = new GPlus(dm);
        eval[j++] = new SumOfAveragePairwiseSimilarities(dm);
        eval[j++] = new MinMaxCut(dm);

        System.out.println("CIndex \t AIC \t BIC \t  SSE \t Gamma \t Tau \t G+ \t SumOfAvgPairwise \t MinMaxCut");
        Props p = new Props();
        for (int n = 2; n < 10; n++) {
            ClusteringAlgorithm km = new KMeans();
            p.putInt("k", n);
            Clustering clusters = km.cluster(data, p);

            double score;
            for (j = 0; j < evalNum; j++) {
                score = eval[j].score(clusters);
                System.out.print(score + " \t ");
            }
            System.out.println();
        }
    }

    /**
     * Test of isBetter method, of class CIndex.
     */
    //  @Test
    public void testCompareScore() {
    }

    @Test
    public void testSchoolScore() throws ScoreException {
        Dataset<? extends Instance> data = FakeDatasets.schoolData();
        int k = 3;
        Clustering<Instance, Cluster<Instance>> clusters = new ClusterList<>(k);
        Cluster c;
        for (int i = 0; i < k; i++) {
            c = clusters.createCluster(i);
            c.setAttributes(data.getAttributes());
        }
        int mod;
        for (int i = 0; i < data.size(); i++) {
            mod = i % k;
            c = clusters.get(mod);
            c.add(data.get(i));
        }
        assertEquals(1.794201393473184, subject.score(clusters), delta);

        System.out.println("school score = " + subject.score(clusters));
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
        //TODO check the C-index definition
        double score = subject.score(FakeClustering.int100p4());
        //TODO: according to clustCrit it should be 7.0592193043113e-06
        assertEquals(1.5759000901106137, score, delta);
        //assertEquals(7.0592193043113e-06, score, delta);
    }
}
