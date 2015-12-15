package org.clueminer.clustering.aggl;

import java.io.IOException;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.aggl.linkage.CompleteLinkage;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HCTest {

    private static final HC subject = new HC();
    private static final double DELTA = 1e-9;

    /**
     * Test of getName method, of class HC1.
     */
    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testColumnClustering() throws IOException {
        Dataset<? extends Instance> dataset = FakeClustering.schoolData();
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.COLUMNS_CLUSTERING);
        pref.put(PropType.PERFORMANCE, AgglParams.KEEP_PROXIMITY, true);
        HierarchicalResult result = subject.hierarchy(dataset, pref);
        Matrix similarityMatrix = result.getProximityMatrix();
        assertNotNull(similarityMatrix);
        assertEquals(similarityMatrix.rowsCount(), dataset.attributeCount());
        assertEquals(similarityMatrix.columnsCount(), dataset.attributeCount());

        result.getTreeData().print();
    }

    @Test
    public void testTriangleSize() {
        assertEquals(3, subject.triangleSize(3));
        assertEquals(6, subject.triangleSize(4));
    }

    @Test
    public void testSingleLinkage() {
        Dataset<? extends Instance> dataset = FakeClustering.kumarData();
        assertEquals(6, dataset.size());
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        pref.put(PropType.PERFORMANCE, AgglParams.KEEP_PROXIMITY, true);
        HierarchicalResult result = subject.hierarchy(dataset, pref);
        Matrix similarityMatrix = result.getProximityMatrix();
        assertNotNull(similarityMatrix);
        assertEquals(similarityMatrix.rowsCount(), dataset.size());
        assertEquals(similarityMatrix.columnsCount(), dataset.size());
        System.out.println("kumar - single");
        DendroTreeData tree = result.getTreeData();
        tree.print();
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();
        assertEquals(0.21587033144922904, root.getHeight(), DELTA);

        int levels = tree.distinctHeights(1e-7);
        //TODO: in this example nodes #7 and #8 are on different level,
        //but their height is the same. should we consider those as different
        assertEquals(4, levels);
    }

    @Test
    public void testSingleLinkageSchool() {
        Dataset<? extends Instance> dataset = FakeClustering.schoolData();
        assertEquals(17, dataset.size());
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        HierarchicalResult result = subject.hierarchy(dataset, pref);
        System.out.println("school - single");
        DendroTreeData tree = result.getTreeData();
        tree.print();
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();
        assertEquals(32.542734980330046, root.getHeight(), DELTA);
        assertEquals(2 * dataset.size() - 1, tree.numNodes());

        assertEquals(16, tree.distinctHeights());
        assertEquals(8, tree.treeLevels());
    }

    @Test
    public void testCompleteLinkageSchool() {
        Dataset<? extends Instance> dataset = FakeClustering.schoolData();
        assertEquals(17, dataset.size());
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, CompleteLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        HierarchicalResult result = subject.hierarchy(dataset, pref);
        System.out.println("school - complete");
        DendroTreeData tree = result.getTreeData();
        tree.print();
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();
        assertEquals(121.11422748793802, root.getHeight(), DELTA);
        assertEquals(2 * dataset.size() - 1, tree.numNodes());

        assertEquals(16, tree.distinctHeights());
        assertEquals(6, tree.treeLevels());
    }

    @Test
    public void testInverseSorting() {
        Dataset<? extends Instance> dataset = FakeClustering.kumarData();
        assertEquals(6, dataset.size());
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        //inverse ordering
        pref.put(AgglParams.SMALLEST_FIRST, false);
        HierarchicalResult result = subject.hierarchy(dataset, pref);
        System.out.println("kumar - inverse");
        DendroTreeData tree = result.getTreeData();
        tree.print();
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();
        assertEquals(0.10198039027185574, root.getHeight(), DELTA);

        assertEquals(5, tree.distinctHeights());
        assertEquals(4, tree.treeLevels());
    }

}
