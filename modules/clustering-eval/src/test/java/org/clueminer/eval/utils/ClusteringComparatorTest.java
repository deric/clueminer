package org.clueminer.eval.utils;

import java.util.Arrays;
import java.util.List;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ClusteringComparatorTest {

    private ClusteringComparator subject = new ClusteringComparator();

    public ClusteringComparatorTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCompare() {
        Clustering[] ary = new Clustering[]{FakeClustering.iris(), FakeClustering.irisMostlyWrong(),
            FakeClustering.irisWrong2(), FakeClustering.irisWrong4()};
        List<ClusterEvaluator> eval = InternalEvaluatorFactory.getInstance().getAll();
        for (ClusterEvaluator e : eval) {
            subject.setEvaluator(e);
            Arrays.sort(ary, subject);
        }
    }

    @Test
    public void testEvaluationTable() {
    }

    @Test
    public void testGetEvaluator() {
    }

    @Test
    public void testSetEvaluator() {
    }

    @Test
    public void testIsAsc() {
    }

    @Test
    public void testSetAsc() {
    }

}
