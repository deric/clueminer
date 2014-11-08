package org.clueminer.eval.external;

import com.google.common.collect.BiMap;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
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
        c1.lookupRemove(BiMap.class);
        double score = subject.score(c1, c2);
        double end = System.currentTimeMillis();

        assertEquals(expected, score, delta);
        System.out.println(subject.getName() + " = " + score);
        System.out.println("measuring " + subject.getName() + " took " + (end - start) + " ms");
        c1.lookupRemove(BiMap.class);
        return score;
    }

    protected double measure(Clustering c1, Dataset<? extends Instance> dataset, double expected) {
        long start = System.currentTimeMillis();
        c1.lookupRemove(BiMap.class);
        double score = subject.score(c1, dataset);
        double end = System.currentTimeMillis();

        assertEquals(expected, score, delta);
        System.out.println(subject.getName() + " = " + score);
        System.out.println("measuring " + subject.getName() + " took " + (end - start) + " ms");
        c1.lookupRemove(BiMap.class);
        return score;
    }

}
