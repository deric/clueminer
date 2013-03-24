package org.clueminer.interpolation;

import org.clueminer.utils.Dump;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author Tomas Barton
 */
public class LinearInterpolatorTest {

    private static LinearInterpolator test = new LinearInterpolator();

    public LinearInterpolatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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
     * Test of getValue method, of class LinearInterpolator.
     */
    @Test
    public void testGetValue() {
        int n = 15;
        double[] x = new double[n];
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = i;
            y[i] = Math.exp(i);
        }
        Dump.array(x, "x");
        Dump.array(y, "y");
        long start, end;
        start = System.nanoTime();
        //assertEquals(12.18, test.getValue(x, y, 2.5, 0, 15), 0.2);
        end = System.nanoTime();
        System.out.println("exp(2.5): time= " + (end - start) + " ns");
        start = System.nanoTime();
        //assertEquals(1468864.2, test.getValue(x, y, 14.2, 0, 15), 100);
        end = System.nanoTime();
        System.out.println("exp(14.2): time= " + (end - start) + " ns");
    }
}