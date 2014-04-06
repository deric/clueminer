package org.clueminer.algorithm;

import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import org.clueminer.dataset.row.DataItem;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;

/**
 *
 * @author deric
 */
@RunWith(ZohhakRunner.class)
public class InterpolationSearchTest {

    private static double delta = 1e-9;

    public InterpolationSearchTest() {
    }

    @TestWith({
        "-1.0, 0.0, 0",
        "0.0, 0.0, 0",
        "0.1, 0.0, 0",
        "0.6, 0.0, 0",
        "0.9, 0.0, 0",
        "1.0, 1.0, 1",
        "3.0, 3.0, 3",
        "3.5, 3.0, 3",})
    public void testSearch_NumericArr_double(double needle, double expValue, int expIdx) {
        int size = 5;
        DataItem[] array = new DataItem[size];
        for (int i = 0; i < size; i++) {
            array[i] = new DataItem(i);
        }
        int index = InterpolationSearch.search(array, needle);
        assertEquals(expValue, array[index].doubleValue(), delta);
        assertEquals(expIdx, index);
    }

}
