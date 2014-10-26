package org.clueminer.clustering.aggl;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class SLINKTest {

    private static Dataset<? extends Instance> school;
    private final SLINK subject = new SLINK();

    public SLINKTest() {
    }

    @Before
    public void setUp() {
        school = FakeClustering.schoolData();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetName() {
    }

    @Test
    public void testCluster_Matrix_Props() {
    }

    @Test
    public void testCluster_Dataset() {
    }

    @Test
    public void testHierarchy_Dataset_Props() {
        subject.hierarchy(school, new Props());

    }

    @Test
    public void testHierarchy_3args() {
    }

    @Test
    public void testHierarchy_Matrix_Props() {
    }

}
