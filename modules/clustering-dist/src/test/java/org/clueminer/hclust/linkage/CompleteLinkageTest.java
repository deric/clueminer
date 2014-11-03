package org.clueminer.hclust.linkage;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CompleteLinkageTest {
    private final CompleteLinkage subject = new CompleteLinkage();
    private static final double delta = 1e-9;


    @Test
    public void testDistance() {
        Dataset<? extends Instance> dataset = FakeDatasets.kumarData();

        Cluster a = new BaseCluster(2);
        a.add(dataset.get(2));
        a.add(dataset.get(5));
        Cluster b = new BaseCluster(2);
        b.add(dataset.get(1));
        b.add(dataset.get(4));

        //max distance between all items in the cluster
        double dist = subject.distance(a, b);
        assertEquals(0.38600518131237566, dist, delta);
        //transitive measure
        double dist2 = subject.distance(b, a);
        assertEquals(dist, dist2, delta);

        Cluster c = new BaseCluster(1);
        c.add(dataset.get(3));
        dist = subject.distance(a, c);
        assertEquals(0.21954498400100148, dist, delta);
    }

    @Test
    public void testSimilarity() {
    }

}
