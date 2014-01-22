package org.clueminer.hclust;

import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class DTreeNodeTest {

    private DendroNode subject;
    private static DendroNode tree;
    private static final double delta = 1e-9;
    public DTreeNodeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        tree = new DTreeNode(true);
        DendroNode current = tree;
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                current.setLeft(new DTreeNode(i));
                current = current.getLeft();
            } else {
                current.setRight(new DTreeNode(i));
                current = current.getRight();
            }
        }
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        subject = new DTreeNode();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of isLeaf method, of class DTreeNode.
     */
    @Test
    public void testIsLeaf() {
        //no children, should be a leaf
        assertEquals(true, subject.isLeaf());
        subject.setLeft(new DTreeNode(subject));
        assertEquals(false, subject.isLeaf());
        subject.setRight(new DTreeNode(subject));
        assertEquals(false, subject.isLeaf());
        subject.setLeft(null);
        assertEquals(false, subject.isLeaf());
    }

    /**
     * Test of isRoot method, of class DTreeNode.
     */
    @Test
    public void testIsRoot() {
        assertEquals(false, subject.isRoot());
        assertEquals(true, tree.isRoot());
    }

    /**
     * Test of getLeft method, of class DTreeNode.
     */
    @Test
    public void testGetLeft() {
    }

    /**
     * Test of hasLeft method, of class DTreeNode.
     */
    @Test
    public void testHasLeft() {
        assertEquals(false, subject.hasLeft());
    }

    /**
     * Test of getRight method, of class DTreeNode.
     */
    @Test
    public void testGetRight() {
    }

    /**
     * Test of hasRight method, of class DTreeNode.
     */
    @Test
    public void testHasRight() {
        assertEquals(false, subject.hasRight());
    }

    /**
     * Test of setLeft method, of class DTreeNode.
     */
    @Test
    public void testSetLeft() {
    }

    /**
     * Test of setRight method, of class DTreeNode.
     */
    @Test
    public void testSetRight() {
    }

    /**
     * Test of level method, of class DTreeNode.
     */
    @Test
    public void testLevel() {
        assertEquals(10, tree.level());
    }

    /**
     * Test of setLevel method, of class DTreeNode.
     */
    @Test
    public void testSetLevel() {
    }

    /**
     * Test of getParent method, of class DTreeNode.
     */
    @Test
    public void testGetParent() {
        assertEquals(null, subject.getParent());
    }

    /**
     * Test of setParent method, of class DTreeNode.
     */
    @Test
    public void testSetParent() {
    }

    /**
     * Test of childCnt method, of class DTreeNode.
     */
    @Test
    public void testChildCnt() {
    }

    /**
     * Test of getHeight method, of class DTreeNode.
     */
    @Test
    public void testGetHeight() {
        assertEquals(0.0, subject.getHeight(), delta);
    }

    /**
     * Test of setHeight method, of class DTreeNode.
     */
    @Test
    public void testSetHeight() {
    }

    /**
     * Test of getPosition method, of class DTreeNode.
     */
    @Test
    public void testGetPosition() {
    }

    /**
     * Test of setPosition method, of class DTreeNode.
     */
    @Test
    public void testSetPosition() {
    }

    /**
     * Test of setId method, of class DTreeNode.
     */
    @Test
    public void testSetId() {
    }

    /**
     * Test of getId method, of class DTreeNode.
     */
    @Test
    public void testGetId() {
    }

    /**
     * Test of toString method, of class DTreeNode.
     */
    @Test
    public void testToString() {
    }

}
