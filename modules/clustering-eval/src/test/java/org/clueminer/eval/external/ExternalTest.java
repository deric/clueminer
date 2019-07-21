/*
 * Copyright (C) 2011-2019 clueminer.org
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

import java.util.Random;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.eval.utils.PairMatch;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Tomas Barton
 */
public class ExternalTest {

    protected ExternalEvaluator subject;
    //floating operation from R is according to IEEE 754, while Java isn't
    protected static final double DELTA = 1e-6;
    protected static Clustering ext100p2;
    protected static Clustering ext100p3;

    public ExternalTest() {
        ext100p2 = FakeClustering.ext100p2();
        ext100p3 = FakeClustering.ext100p3();
    }

    protected double measure(Clustering c1, Clustering c2, double expected) throws ScoreException {
        long start = System.currentTimeMillis();
        c1.lookupRemove(PairMatch.class);
        double score = subject.score(c1, c2, new Props());
        double end = System.currentTimeMillis();
        assertEquals(expected, score, DELTA);
        System.out.println(subject.getName() + " = " + score);
        System.out.println("measuring " + subject.getName() + " took " + (end - start) + " ms");
        c1.lookupRemove(PairMatch.class);
        return score;
    }

    protected double measure(Clustering c1, double expected) throws ScoreException {
        long start = System.currentTimeMillis();
        c1.lookupRemove(PairMatch.class);
        double score = subject.score(c1);
        double end = System.currentTimeMillis();

        assertEquals(expected, score, DELTA);
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
    public Clustering<Instance, Cluster<Instance>> pcaData() {
        Clustering<Instance, Cluster<Instance>> clustering = new ClusterList<>(3);
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

    public Clustering<Instance, Cluster<Instance>> oneClassPerCluster() {
        Clustering<Instance, Cluster<Instance>> oneClass = new ClusterList(3);
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
