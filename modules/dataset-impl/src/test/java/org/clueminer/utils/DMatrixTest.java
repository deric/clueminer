package org.clueminer.utils;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DMatrixTest {

    private static double[][] data = new double[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}};
    private static DMatrix subject;
    private static double delta = 1e-9;

    public DMatrixTest() {
        Dataset<? extends Instance> dataset = new ArrayDataset(data);
        subject = new DMatrix(dataset);
    }

    @Test
    public void testSetup() {
        subject.print(2, 0);
    }

    @Test
    public void testGetArrayCopy() {
    }

    @Test
    public void testRowsCount() {
        assertEquals(2, subject.rowsCount());
    }

    @Test
    public void testColumnsCount() {
        assertEquals(5, subject.columnsCount());
    }

    @Test
    public void testGet() {
        assertEquals(1, subject.get(0, 0), delta);
    }

    @Test
    public void testCopy() {
    }

    @Test
    public void testGetColumnPackedCopy() {
    }

    @Test
    public void testGetRowPackedCopy() {
    }

    @Test
    public void testTranspose() {
    }

    @Test
    public void testGetMatrix_4args() {
    }

    @Test
    public void testGetMatrix_intArr_intArr() {
    }

    @Test
    public void testGetMatrix_3args_1() {
    }

    @Test
    public void testGetMatrix_3args_2() {
    }

    @Test
    public void testSet() {
    }

    @Test
    public void testSetMatrix_5args() {
    }

    @Test
    public void testSetMatrix_3args() {
    }

    @Test
    public void testSetMatrix_4args_1() {
    }

    @Test
    public void testSetMatrix_4args_2() {
    }

    @Test
    public void testNormInf() {
    }

    @Test
    public void testNormF() {
    }

    @Test
    public void testUminus() {
    }

    @Test
    public void testPlus() {
    }

    @Test
    public void testPlusEquals() {
    }

    @Test
    public void testMinus() {
    }

    @Test
    public void testMinusEquals() {
    }

    @Test
    public void testArrayTimes() {
    }

    @Test
    public void testArrayTimesEquals() {
    }

    @Test
    public void testArrayRightDivide() {
    }

    @Test
    public void testArrayRightDivideEquals() {
    }

    @Test
    public void testArrayLeftDivide() {
    }

    @Test
    public void testArrayLeftDivideEquals() {
    }

    @Test
    public void testTimes_double() {
    }

    @Test
    public void testTimesEquals() {
    }

    @Test
    public void testTimes_Matrix() {
    }

    @Test
    public void testTrace() {
    }

}
