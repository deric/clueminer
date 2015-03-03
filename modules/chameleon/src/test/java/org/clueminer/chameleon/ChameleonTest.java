package org.clueminer.chameleon;

import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class ChameleonTest {

    @Test
    public void testGetName() {
        Chameleon ch = new Chameleon();
        assertEquals("Chameleon", ch.getName());
    }

    @Test
    public void treeTest() {
        Chameleon ch = new Chameleon(40, 10, true, MergingStrategy.PAIR);
        ch.hierarchy(FakeDatasets.irisDataset(), null);
    }

}
