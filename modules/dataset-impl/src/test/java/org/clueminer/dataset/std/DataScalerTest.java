package org.clueminer.dataset.std;

import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
import org.clueminer.std.StdAbsDev;
import org.clueminer.std.StdDev;
import org.clueminer.std.StdScale;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DataScalerTest {

    private static final double[][] data1 = new double[][]{
        {1, 2, 3},
        {2, 4, 5},
        {5, 3, 1}
    };

    private static final double[][] data2 = new double[][]{
        {6.5, 3.8, 6.6, 5.7, 6.0, 6.4, 5.3},};

    private static final double delta = 1e-9;
    private static Dataset<? extends Instance> kumar;

    @Test
    public void testStandartize() {
        run(data1, StdDev.name, true);
        run(data1, StdScale.name, true);
        run(data1, StdMax.name, true);
        run(data1, StdAbsDev.name, true);
    }

    @Test
    public void testSchool() {
        run(kumarData(), StdDev.name, false);
        run(kumarData(), StdScale.name, false);
        run(kumarData(), StdMax.name, false);
        run(kumarData(), StdAbsDev.name, false);
    }

    private void run(double[][] data, String method, boolean log) {
        System.out.println(method);
        Matrix res = Scaler.standartize(data, method, log);

        Dataset<? extends Instance> dataset = new ArrayDataset(data);
        DataScaler<Instance> ds = new DataScaler();
        Dataset<? extends Instance> out = ds.standartize((Dataset<Instance>) dataset, method, log);

        for (int i = 0; i < dataset.size(); i++) {
            for (int j = 0; j < dataset.attributeCount(); j++) {
                assertEquals(res.get(i, j), out.get(i, j), delta);
            }
        }
        assertNotNull(out.getParent());
        assertEquals(dataset, out.getParent());
        assertEquals(6, out.getClasses().size());
    }

    private void run(Dataset<? extends Instance> dataset, String method, boolean log) {
        System.out.println(method);
        Matrix res = Scaler.standartize(dataset.arrayCopy(), method, log);
        DataScaler<Instance> ds = new DataScaler();
        Dataset<? extends Instance> out = ds.standartize((Dataset<Instance>) dataset, method, log);

        for (int i = 0; i < dataset.size(); i++) {
            assertEquals(dataset.get(i).getName(), out.get(i).getName());
            assertEquals(dataset.get(i).getId(), out.get(i).getId());
            assertEquals(dataset.get(i).getIndex(), out.get(i).getIndex());
            assertEquals(dataset.get(i).classValue(), out.get(i).classValue());
            /*for (int j = 0; j < dataset.attributeCount(); j++) {
             assertEquals(res.get(i, j), out.get(i, j), delta);
             }*/
        }
        assertNotNull(out.getParent());
        assertEquals(dataset, out.getParent());

    }

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

}
