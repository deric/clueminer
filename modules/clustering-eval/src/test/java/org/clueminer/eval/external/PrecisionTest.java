package org.clueminer.eval.external;

import com.google.common.collect.Table;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author tombart
 */
public class PrecisionTest extends ExternalTest {

    public PrecisionTest() throws FileNotFoundException, IOException {
        subject = new Precision();
    }

    /**
     * Test of score method, of class Precision.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        //this is fixed clustering which correspods to true classes in dataset
        measure(FakeClustering.iris(), FakeDatasets.irisDataset(), 1.0);
    }

    /**
     * Test of isBetter method, of class Precision.
     */
    @Test
    public void testCompareScore() {
        //first one should be better
        assertEquals(true, subject.isBetter(1.0, 0.4));
    }

    @Test
    public void testOneClassPerCluster() {
        Clustering<Cluster> oneClass = new ClusterList(3);
        int size = 3;
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

    /**
     * Test of score method, of class Precision.
     */
    @Test
    public void testScore_Clustering_Clustering() {
        double score;
        measure(FakeClustering.iris(), FakeDatasets.irisDataset(), 1.0);

        measure(FakeClustering.irisWrong4(), FakeClustering.iris(), 0.8666666666666667);
        measure(FakeClustering.irisWrong5(), FakeClustering.iris(), 0.6666666666666667);

        //each cluster should have this scores:
        //Cabernet = 0.6923
        //Syrah = 0.5555
        //Pinot = 0.8000
        score = measure(FakeClustering.wineClustering(), FakeClustering.wineCorrect(), 0.6826210826210826);
        //when using class labels, result should be the same
        measure(FakeClustering.wineClustering(), FakeClustering.wine(), score);
    }

    /**
     * Test of countScore method, of class Precision.
     */
    @Test
    public void testCountScore() {
        double score = subject.score(FakeClustering.irisMostlyWrong());
        System.out.println("mostly wrong precision  = " + score);
        Table<String, String, Integer> table = CountingPairs.contingencyTable(FakeClustering.irisMostlyWrong());
        CountingPairs.dumpTable(table);

        assertEquals(true, score < 0.7);
    }
}
