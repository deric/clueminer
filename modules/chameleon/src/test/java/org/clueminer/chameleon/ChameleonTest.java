package org.clueminer.chameleon;

import org.clueminer.clustering.api.AgglParams;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
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
        Props pref = new Props();
        pref.putBoolean(AgglParams.CLUSTER_COLUMNS, false);
        Chameleon ch = new Chameleon();
        ch.setK(5);
        ch.setStandardMeasure();
        ch.setClosenessPriority(0.5);
        ch.hierarchy(FakeDatasets.irisDataset(), pref);
    }

}
