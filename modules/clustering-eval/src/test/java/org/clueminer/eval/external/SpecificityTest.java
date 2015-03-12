package org.clueminer.eval.external;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class SpecificityTest extends ExternalTest {

    public SpecificityTest() {
        subject = new Specificity();
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
        System.out.println("specificity (mw) = " + score);
        assertEquals(true, score < 0.7);
    }

    @Test
    public void testIrisCorrect() {
        //this is fixed clustering which correspods to true classes in dataset
        measure(FakeClustering.iris(), FakeDatasets.irisDataset(), 1.0);
    }
}
