package org.clueminer.hclust;

import java.io.IOException;
import java.io.OutputStreamWriter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class DynamicTreeDataTest {

    private DynamicTreeData subject;

    public DynamicTreeDataTest() {
        subject = new DynamicTreeData();
        subject.setRoot(new DTreeNode(true));
        subject.getRoot().setId(0);
        subject.getRoot().setLeft(new DTreeNode(1));
        subject.getRoot().setRight(new DTreeNode(2));
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testNumLeaves() {
        assertEquals(2, subject.numLeaves());
    }

    @Test
    public void testTreeLevels() {
        assertEquals(1, subject.treeLevels());
    }

    @Test
    public void testNumNodes() {
        //total number of nodes in the tree
        assertEquals(3, subject.numNodes());
    }

    @Test
    public void testGetRoot() {
        assertEquals(0, subject.getRoot().getId());
    }

    @Test
    public void testSetRoot() {
    }

    @Test
    public void testFirst() {
    }

    @Test
    public void testPrintPaths() {
        subject.printPaths();
    }

    @Test
    public void testPrint() {
        try {
            OutputStreamWriter out = new OutputStreamWriter(System.out);
            subject.printTree(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
    }

}
