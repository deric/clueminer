package org.clueminer.eval.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class JaccardIndexTest extends ExternalTest {

    private static Clustering irisCorrect;
    private static Clustering irisWrong;

    public JaccardIndexTest() throws FileNotFoundException, IOException {
        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong();
        subject = new JaccardIndex();
    }

    /**
     * Test of score method, of class JaccardIndex.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        measure(irisCorrect, 1.0);

        measure(irisWrong, 0.15032686686154664);

        measure(FakeClustering.irisWrong2(), 0.3666666666666667);
    }

    /**
     * Test of isBetter method, of class JaccardIndex.
     */
    @Test
    public void testCompareScore() {
    }

    /**
     * Test of score method, of class JaccardIndex.
     */
    @Test
    public void testScore_Clustering_Clustering() {
        double score;
        //each cluster should have this scores:
        //Cabernet = 0.5625
        //Syrah = 0.3846
        //Pinot = 0.5714
        score = measure(FakeClustering.wineClustering(), FakeClustering.wineCorrect(), 0.5061813186813187);

        //when using class labels result should be the same
        measure(FakeClustering.wineClustering(), score);
    }

    @Test
    public void testOneClassPerCluster() {
        Clustering<Cluster> oneClass = new ClusterList(3);
        int size = 10;
        Dataset<? extends Instance> data = new ArrayDataset<>(size, 2);
        data.attributeBuilder().create("x1", "NUMERIC");
        data.attributeBuilder().create("x2", "NUMERIC");

        for (int i = 0; i < size; i++) {
            Instance inst = data.builder().create(new double[]{1, 2}, "same class");
            //cluster with single class
            BaseCluster clust = new BaseCluster(1);
            clust.add(inst);
            oneClass.add(clust);
        }
        assertEquals(0.0, subject.score(oneClass), delta);
    }

    @Test
    public void testMostlyWrong() {
        double score = subject.score(FakeClustering.irisMostlyWrong());
        System.out.println("jaccard (mw): " + score);
        assertEquals(true, score < 0.3);
    }
}
