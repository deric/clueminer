package org.clueminer.interpolation;

import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author Tomas Barton
 */
public class LagrangeInterpolatorTest {

 private static LagrangeInterpolator test;

    public LagrangeInterpolatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        test = new LagrangeInterpolator();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getValue method, of class LarangeInterpolator.
     */
    @Test
    public void testGetValue() {
        int n = 15;
        double[] x = new double[n];
        double[] y = new double[n];
        for(int i =0; i < n; i++){
            x[i] = i;
            y[i] = Math.exp(i);
        }
        long start, end;
        start = System.nanoTime();
        test.setX(x);
        test.setY(y);
        assertEquals(12.18, test.value(2.5, 0, 0), 0.2);
        end = System.nanoTime();
        System.out.println("exp(2.5): time= "+(end - start)+" ns");
        start = System.nanoTime();
        assertEquals(1468864.2, test.value(14.2, 0, 0), 100);
        end = System.nanoTime();
        System.out.println("exp(14.2): time= "+(end - start)+" ns");
    }

}