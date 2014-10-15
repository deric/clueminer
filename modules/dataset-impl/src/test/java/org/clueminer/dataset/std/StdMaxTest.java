package org.clueminer.dataset.std;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class StdMaxTest {

    private static final double[][] data = new double[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}};
    private static final double[][] data2 = new double[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, -10}, {-5, -2, 19, 1, 5}};
    private static final double delta = 1e-9;
    private static final StdMax subject = new StdMax();
    private static final String method = StdMax.name;

    public StdMaxTest() {
    }

    @Test
    public void testOptimizeData() {
        Matrix res = Scaler.standartize(data, method, false);

        Dataset<? extends Instance> dataset = new ArrayDataset(data);
        Dataset<? extends Instance> out = subject.optimize(dataset);

        assertEquals(dataset, out.getParent());

        for (int i = 0; i < dataset.size(); i++) {
            for (int j = 0; j < dataset.attributeCount(); j++) {
                assertEquals(res.get(i, j), out.get(i, j), delta);
            }
        }
    }

    @Test
    public void testOptimizeData2() {
        Matrix res = Scaler.standartize(data2, method, false);

        Dataset<? extends Instance> dataset = new ArrayDataset(data2);
        Dataset<? extends Instance> out = subject.optimize(dataset);

        for (int i = 0; i < dataset.size(); i++) {
            for (int j = 0; j < dataset.attributeCount(); j++) {
                assertEquals(res.get(i, j), out.get(i, j), delta);
            }
        }
    }

}
