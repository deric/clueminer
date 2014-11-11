package org.clueminer.eval.utils;

import java.util.Arrays;
import java.util.List;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.row.DataItem;
import org.clueminer.math.Numeric;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Simple test for ensuring that all scores behaves correctly when sorting
 * values
 *
 * @author deric
 */
public class ScoreComparatorTest {

    private static final ScoreComparator subject = new ScoreComparator();
    private static final double delta = 1e-9;

    @Test
    public void testCompare() {
        double[] values = new double[]{15, 8, 2.3, 3.14, 155, 4};
        sortArray(values, 2.3, 155);
    }

    private void sortArray(double[] values, double min, double max) {
        //Dump.array(values, "test array");
        Numeric ary[] = new Numeric[values.length];
        for (int i = 0; i < ary.length; i++) {
            ary[i] = new DataItem(values[i]); //just wrapper around double
        }
        List<ClusterEvaluator> eval = InternalEvaluatorFactory.getInstance().getAll();
        for (ClusterEvaluator e : eval) {
            subject.setEvaluator(e);
            System.out.println("testing " + e.getName() + " maximized: " + e.isMaximized());
            Arrays.sort(ary, subject);
            //System.out.println(Arrays.toString(ary));
            if (e.isMaximized()) {
                //first value is the best
                assertEquals(max, ary[0].getValue(), delta);
                assertEquals(min, ary[ary.length - 1].getValue(), delta);
            } else {
                assertEquals(min, ary[0].getValue(), delta);
                assertEquals(max, ary[ary.length - 1].getValue(), delta);
            }
        }
    }

}
