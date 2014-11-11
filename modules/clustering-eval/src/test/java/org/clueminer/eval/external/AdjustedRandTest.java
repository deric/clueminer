package org.clueminer.eval.external;

import com.google.common.collect.Table;
import java.util.Set;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AdjustedRandTest extends ExternalTest {

    public AdjustedRandTest() {
        subject = new AdjustedRand();
    }

    /**
     * Test of isBetter method, of class AdjustedRand.
     */
    @Test
    public void testCompareScore() {
        assertTrue(subject.isBetter(1.0, 0.5));
    }

    /**
     * Test of score method, of class AdjustedRand.
     */
    @Test
    public void testScore_Clustering_Clustering() {
        double score;

        score = measure(FakeClustering.wineClustering(), FakeClustering.wineCorrect(), 0.13473684210526315);
        measure(FakeClustering.wineClustering(), FakeClustering.wine(), score);
    }

    /**
     * Test based on Details of the Adjusted Rand index and Clustering
     * algorithms Supplement to the paper “An empirical study on Principal
     * Component Analysis for clustering gene expression data” (to appear in
     * Bioinformatics)
     *
     * @see http://faculty.washington.edu/kayee/pca/supp.pdf
     * @see http://faculty.washington.edu/kayee/pca/pca.pdf
     *
     */
    @Test
    public void testCountScore() {
        //Table<String, String, Integer> table = CountingPairs.newTable();
        //fake contingency table

        int[][] mat = new int[4][4];
        mat[0][0] = 1;
        mat[0][1] = 1;
        mat[0][2] = 0;
        mat[0][3] = 2; //sum of row[0]
        mat[1][0] = 1;
        mat[1][1] = 2;
        mat[1][2] = 1;
        mat[1][3] = 4; //sum of row[1]
        mat[2][0] = 0;
        mat[2][1] = 0;
        mat[2][2] = 4;
        mat[2][3] = 4;//sum of row[2]
        //col sums
        mat[3][0] = 2;
        mat[3][1] = 3;
        mat[3][2] = 5;
        mat[3][3] = 10;

        AdjustedRand rand = (AdjustedRand) subject;
        double score = rand.countScore(mat);
        System.out.println("score: " + score);
        assertEquals(0.31257344300822565, score, delta);
    }

    @Test
    public void testScoreDataset() {
        Clustering<Cluster> clustering = FakeClustering.irisWrong4();
        double score = subject.score(clustering, FakeDatasets.irisDataset());
        System.out.println("clust(4) = " + score);
    }

    @Test
    public void testIris() {
        Clustering<Cluster> clustering = FakeClustering.irisWrong5();
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clustering);
        dumpTable(table);
        AdjustedRand rand = (AdjustedRand) subject;
        double score = rand.countScore(table);
        //value based on experiments (not verified yet) - just to verify that we didnt break the functionality
        assertEquals(0.14754877843024122, score, delta);
        System.out.println("clust(5) = " + score);
    }

    public void dumpTable(Table<String, String, Integer> table) {
        StringBuilder sb = new StringBuilder();
        Set<String> rows = table.columnKeySet();
        Set<String> cols = table.rowKeySet();
        String separator = "   ";
        //print header
        sb.append(separator);
        for (String col : cols) {
            sb.append(col);
            sb.append(separator);
        }
        sb.append("\n");
        for (String row : rows) {
            sb.append(row);
            sb.append(separator);
            for (String col : cols) {
                sb.append(table.get(col, row));
                sb.append(separator);
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }
}
