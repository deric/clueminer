package org.clueminer.dataset.std;

import java.io.IOException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.CsvLoader;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
import org.clueminer.std.StdAbsDev;
import org.clueminer.std.StdDev;
import org.clueminer.std.StdScale;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.openide.util.Exceptions;

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
    private static Dataset<? extends Instance> school;
    private static final CommonFixture fixture = new CommonFixture();

    @Test
    public void testStandartize() {
        run(data1, StdDev.name, true);
        run(data1, StdScale.name, true);
        run(data1, StdMax.name, true);
        run(data1, StdAbsDev.name, true);
    }

    @Test
    public void testSchool() {
        run(schoolData(), StdDev.name, false);
        run(schoolData(), StdScale.name, false);
        run(schoolData(), StdMax.name, false);
        run(schoolData(), StdAbsDev.name, false);
    }

    private void run(double[][] data, String method, boolean log) {
        System.out.println(method);
        Matrix res = Scaler.standartize(data, method, log);

        Dataset<? extends Instance> dataset = new ArrayDataset(data);
        Dataset<? extends Instance> out = DataScaler.standartize(dataset, method, log);

        for (int i = 0; i < dataset.size(); i++) {
            for (int j = 0; j < dataset.attributeCount(); j++) {
                assertEquals(res.get(i, j), out.get(i, j), delta);
            }
        }
        assertNotNull(out.getParent());
        assertEquals(dataset, out.getParent());

    }

    private void run(Dataset<? extends Instance> dataset, String method, boolean log) {
        System.out.println(method);
        Matrix res = Scaler.standartize(dataset.arrayCopy(), method, log);
        Dataset<? extends Instance> out = DataScaler.standartize(dataset, method, log);

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

    public static Dataset<? extends Instance> schoolData() {
        if (school == null) {
            CsvLoader loader = new CsvLoader();
            school = new ArrayDataset(17, 4);
            loader.setClassIndex(4);
            loader.setSeparator(' ');
            try {
                loader.load(fixture.schoolData(), school);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return school;

    }

}
