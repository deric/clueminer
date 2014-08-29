package org.clueminer.eval;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.PartitioningClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.clueminer.io.FileHandler;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class CIndexTest {

    public CIndexTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of score method, of class CIndex.
     *
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testScore() throws IOException, FileNotFoundException {
        CommonFixture tf = new CommonFixture();
        Dataset data = new SampleDataset();
        data.setAttribute(0, data.attributeBuilder().create("x", BasicAttrType.NUMERICAL));
        data.setAttribute(0, data.attributeBuilder().create("y", BasicAttrType.NUMERICAL));
        ARFFHandler arff = new ARFFHandler();
        assertTrue(arff.load(tf.simpleCluster(), data, 2));

        DistanceMeasure dist = new EuclideanDistance();

        ClusterEvaluation cind = new CIndex(dist);
        ClusterEvaluation aic = new AICScore();
        ClusterEvaluation bic = new BICScore();
        ClusterEvaluation sse = new SumOfSquaredErrors(dist);
        ClusterEvaluation gamma = new Gamma(dist);

        System.out.println("\t CIndex \t AIC \t BIC \t  SSE \t Gamma");
        for (int n = 1; n < 10; n++) {
            PartitioningClustering km = new KMeans(n, 100, new EuclideanDistance());
            Clustering clusters = km.partition(data);

            double cindScore = cind.score(clusters, data);
            double aicScore = aic.score(clusters, data);
            double bicScore = bic.score(clusters, data);
            double sseScore = sse.score(clusters, data);
            double gScore = gamma.score(clusters, data);
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
         * "\t" + aic.compareScore(aicScore3, aicScore4)); //assertEquals(true,
         * aic.compareScore(aicScore3, aicScore4)); System.out.println("BIC
         * score: \t" + bicScore3 + "\t" + bicScore4 + "\t" +
         * bic.compareScore(bicScore3, bicScore4)); // assertEquals(true,
         * bic.compareScore(bicScore3, bicScore4)); System.out.println("SQ
         * error: \t" + sseScore3 + "\t" + sseScore4 + "\t" +
         * sse.compareScore(sseScore3, sseScore4)); // assertEquals(true,
         * sse.compareScore(sseScore3, sseScore4)); System.out.println("CIndex:
         * \t" + k3res + "\t" + k4res + "\t" + cind.compareScore(k3res, k4res));
         * System.out.println("Gamma: \t" + gScore2 + "\t" + gScore3 + "\t" +
         * cind.compareScore(gScore2, gScore3));
         */
        /**
         * We know, that in iris dataset there are 3 true classes, so first
         * score should be better
         */
        //assertEquals(true, cind.compareScore(k3res, k4res));
    }

    @Test
    public void testIris() throws IOException {
        CommonFixture tf = new CommonFixture();
        Dataset data = new SampleDataset();
        data.setAttribute(0, data.attributeBuilder().create("sepal length", BasicAttrType.NUMERICAL));
        data.setAttribute(1, data.attributeBuilder().create("sepal width", BasicAttrType.NUMERICAL));
        data.setAttribute(2, data.attributeBuilder().create("petal length", BasicAttrType.NUMERICAL));
        data.setAttribute(3, data.attributeBuilder().create("petal width", BasicAttrType.NUMERICAL));

        assertTrue(FileHandler.loadDataset(tf.irisData(), data, 4, ","));
        int evalNum = 9;
        ClusterEvaluation[] eval = new ClusterEvaluation[evalNum];
        int j = 0;
        DistanceMeasure dm = new EuclideanDistance();
        eval[j++] = new CIndex(dm);
        eval[j++] = new AICScore();
        eval[j++] = new BICScore();
        eval[j++] = new SumOfSquaredErrors(dm);
        eval[j++] = new Gamma(dm);
        eval[j++] = new Tau(dm);
        eval[j++] = new GPlus(dm);
        eval[j++] = new SumOfAveragePairwiseSimilarities(dm);
        eval[j++] = new MinMaxCut(dm);

        System.out.println("CIndex \t AIC \t BIC \t  SSE \t Gamma \t Tau \t G+ \t SumOfAvgPairwise \t MinMaxCut");
        for (int n = 1; n < 10; n++) {
            PartitioningClustering km = new KMeans(n, 100, new EuclideanDistance());
            Clustering clusters = km.partition(data);

            double score;
            for (j = 0; j < evalNum; j++) {
                score = eval[j].score(clusters, data);
                System.out.print(score + " \t ");
            }
            System.out.println();
        }
    }

    /**
     * Test of compareScore method, of class CIndex.
     */
    @Test
    public void testCompareScore() {
    }
}
