package org.clueminer.eval.utils;

import java.util.Arrays;
import java.util.List;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ClusteringComparatorTest {

    private ClusteringComparator subject = new ClusteringComparator();

    @Test
    public void testCompare() {
        Clustering[] ary = new Clustering[]{FakeClustering.iris(), FakeClustering.irisMostlyWrong(),
            FakeClustering.irisWrong2(), FakeClustering.irisWrong4()};
        List<InternalEvaluator> eval = InternalEvaluatorFactory.getInstance().getAll();
        for (InternalEvaluator e : eval) {
            subject.setEvaluator(e);
            Arrays.sort(ary, subject);
        }
    }

}
