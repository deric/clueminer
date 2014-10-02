package org.clueminer.std;

import org.clueminer.math.Matrix;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ScalerTest {

    private static final double[][] data1 = new double[][]{
        {1, 2, 3},
        {2, 4, 5},
        {5, 3, 1}
    };

    private static final double[][] data2 = new double[][]{
        {6.5, 3.8, 6.6, 5.7, 6.0, 6.4, 5.3},};

    public ScalerTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testStandartize() {
        Matrix res;
        res = run(data1, StdDev.name, false);
        res = run(data1, StdScale.name, false);
        res = run(data1, StdMax.name, false);
        res = run(data1, StdAbsDev.name, false);
    }

    @Test
    public void testStandartize2() {
        Matrix res;
        res = run(data2, StdDev.name, false);
        res = run(data2, StdScale.name, false);
        res = run(data2, StdMax.name, false);
        res = run(data2, StdAbsDev.name, false);
    }

    private Matrix run(double[][] data, String method, boolean log) {
        System.out.println(method);
        Matrix res = Scaler.standartize(data, method, log);
        res.print(2, 3);
        return res;
    }

    @Test
    public void testLogScale() {
    }

}
