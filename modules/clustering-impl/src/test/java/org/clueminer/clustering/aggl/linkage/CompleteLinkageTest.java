package org.clueminer.clustering.aggl.linkage;

import org.clueminer.attributes.BasicAttrType;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CompleteLinkageTest {
    private final CompleteLinkage subject = new CompleteLinkage();
    private static final double delta = 1e-9;
    private static Dataset<? extends Instance> kumar;

    /**
     * Testing dataset from Introduction to Data-mining, Tan, Kumar (chapter 8,
     * page 519)
     *
     * @return
     */
    public static Dataset<? extends Instance> kumarData() {
        if (kumar == null) {
            kumar = new ArrayDataset<>(4, 2);
            kumar.attributeBuilder().create("x", BasicAttrType.NUMERIC);
            kumar.attributeBuilder().create("y", BasicAttrType.NUMERIC);
            kumar.builder().create(new double[]{0.40, 0.53}, "1");
            kumar.builder().create(new double[]{0.22, 0.38}, "2");
            kumar.builder().create(new double[]{0.35, 0.32}, "3");
            kumar.builder().create(new double[]{0.26, 0.19}, "4");
            kumar.builder().create(new double[]{0.08, 0.41}, "5");
            kumar.builder().create(new double[]{0.45, 0.30}, "6");
        }
        return kumar;
    }

    @Test
    public void testDistance() {
        Dataset<? extends Instance> dataset = kumarData();

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
    public void testAlphaA() {
        assertEquals(0.5, subject.alphaA(1, 3, 1), delta);
    }

    @Test
    public void testAlphaB() {
        assertEquals(0.5, subject.alphaB(1, 3, 1), delta);
    }

    @Test
    public void testBeta() {
        assertEquals(0.0, subject.beta(1, 2, 3), delta);
    }

    @Test
    public void testGamma() {
        assertEquals(0.5, subject.gamma(), delta);
    }

}
