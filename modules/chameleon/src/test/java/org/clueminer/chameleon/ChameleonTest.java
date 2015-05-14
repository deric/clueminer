package org.clueminer.chameleon;

import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class ChameleonTest {

    double delta = 1e-9;

    @Test
    public void testGetName() {
        Chameleon ch = new Chameleon();
        assertEquals("Chameleon", ch.getName());
    }

    @Test
    public void testIrisStandard() {
        Props pref = new Props();
        pref.putBoolean(AgglParams.CLUSTER_COLUMNS, false);
        Chameleon ch = new Chameleon();
        pref.putInt(Chameleon.K, 5);
        pref.put(Chameleon.SIM_MEASURE, SimilarityMeasure.STANDARD);
        pref.putDouble(Chameleon.CLOSENESS_PRIORITY, 0.5);
        HierarchicalResult result = ch.hierarchy(FakeDatasets.irisDataset(), pref);
        DendroTreeData tree = result.getTreeData();
        tree.print();
        DendroNode root = tree.getRoot();
        assertEquals(363.3346235252453, root.getHeight(), delta);
    }

    @Test
    public void testIrisImproved() {
        Props pref = new Props();
        pref.putBoolean(AgglParams.CLUSTER_COLUMNS, false);
        Chameleon ch = new Chameleon();
        pref.putInt(Chameleon.K, 5);
        pref.put(Chameleon.SIM_MEASURE, SimilarityMeasure.IMPROVED);
        pref.putDouble(Chameleon.CLOSENESS_PRIORITY, 0.5);
        HierarchicalResult result = ch.hierarchy(FakeDatasets.irisDataset(), pref);
        DendroTreeData tree = result.getTreeData();
        tree.print();
        DendroNode root = tree.getRoot();
        assertEquals(396.2089980350674, root.getHeight(), delta);
    }

    @Test
    public void testSchool() {
        Props pref = new Props();
        pref.putBoolean(AgglParams.CLUSTER_COLUMNS, false);
        Chameleon ch = new Chameleon();
        HierarchicalResult result = ch.hierarchy(FakeDatasets.schoolData(), pref);
        DendroTreeData tree = result.getTreeData();
        tree.print();
        DendroNode root = tree.getRoot();
        assertEquals(60.40222184544098, root.getHeight(), delta);
    }

    @Test
    public void testSchoolClosenessPriority() {
        Props pref = new Props();
        pref.putBoolean(AgglParams.CLUSTER_COLUMNS, false);
        Chameleon ch = new Chameleon();
        pref.putDouble(Chameleon.CLOSENESS_PRIORITY, 4);
        HierarchicalResult result = ch.hierarchy(FakeDatasets.schoolData(), pref);
        DendroTreeData tree = result.getTreeData();
        tree.print();
        DendroNode root = tree.getRoot();
        assertEquals(166.38835528693502, root.getHeight(), delta);
    }

}
