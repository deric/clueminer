package org.clueminer.eval.external;

import java.util.Random;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.eval.utils.PairMatch;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Tomas Barton
 */
public class ExternalTest {

    protected ExternalEvaluator subject;
    protected static final double delta = 1e-9;

    protected double measure(Clustering c1, Clustering c2, double expected) {
        long start = System.currentTimeMillis();
        c1.lookupRemove(PairMatch.class);
        double score = subject.score(c1, c2, new Props());
        double end = System.currentTimeMillis();

        assertEquals(expected, score, delta);
        System.out.println(subject.getName() + " = " + score);
        System.out.println("measuring " + subject.getName() + " took " + (end - start) + " ms");
        c1.lookupRemove(PairMatch.class);
        return score;
    }

    protected double measure(Clustering c1, double expected) {
        long start = System.currentTimeMillis();
        c1.lookupRemove(PairMatch.class);
        double score = subject.score(c1);
        double end = System.currentTimeMillis();

        assertEquals(expected, score, delta);
        System.out.println(subject.getName() + " = " + score);
        System.out.println("measuring " + subject.getName() + " took " + (end - start) + " ms");
        c1.lookupRemove(PairMatch.class);
        return score;
    }

    /**
     * Based on "Details of the Adjusted Rand index and Clustering algorithms
     * Supplement to the paper “An empirical study on Principal Component
     * Analysis for clustering gene expression data” (to appear in
     * Bioinformatics)"
     *
     * @return
     */
    public Clustering<? extends Cluster> pcaData() {
        Clustering<Cluster> clustering = new ClusterList(3);
        Random rand = new Random();
        int size = 10;
        Dataset<? extends Instance> data = new ArrayDataset<>(size, 2);
        data.attributeBuilder().create("x1", "NUMERIC");
        data.attributeBuilder().create("x2", "NUMERIC");

        InstanceBuilder<? extends Instance> builder = data.builder();
        BaseCluster c1 = new BaseCluster(2);
        clustering.add(c1);
        BaseCluster c2 = new BaseCluster(3);
        clustering.add(c2);
        BaseCluster c3 = new BaseCluster(5);
        clustering.add(c3);

        c1.add(next(rand, builder, "u1"));
        c1.add(next(rand, builder, "u2"));
        c2.add(next(rand, builder, "u1"));
        c2.add(next(rand, builder, "u2"));
        c2.add(next(rand, builder, "u2"));
        c3.add(next(rand, builder, "u2"));
        c3.add(next(rand, builder, "u3"));
        c3.add(next(rand, builder, "u3"));
        c3.add(next(rand, builder, "u3"));
        c3.add(next(rand, builder, "u3"));

        clustering.lookupAdd(data);
        return clustering;
    }

    public Instance next(Random rand, InstanceBuilder<? extends Instance> builder, String klass) {
        return builder.create(new double[]{rand.nextDouble(), rand.nextDouble()}, klass);
    }

    public Clustering<? extends Cluster> oneClassPerCluster() {
        Clustering<Cluster> oneClass = new ClusterList(3);
        int size = 10;
        Random rand = new Random();
        Dataset<? extends Instance> data = new ArrayDataset<>(size, 2);
        data.attributeBuilder().create("x1", "NUMERIC");
        data.attributeBuilder().create("x2", "NUMERIC");

        for (int i = 0; i < size; i++) {
            Instance inst = next(rand, data.builder(), "same class");
            //cluster with single class
            BaseCluster clust = new BaseCluster(1);
            clust.add(inst);
            oneClass.add(clust);
        }
        oneClass.lookupAdd(data);
        return oneClass;
    }

}
