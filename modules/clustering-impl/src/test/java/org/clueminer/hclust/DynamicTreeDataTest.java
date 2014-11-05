package org.clueminer.hclust;

import java.io.IOException;
import java.io.OutputStreamWriter;
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
        DynamicTreeData test = new DynamicTreeData();
        test.setRoot(new DTreeNode(true));
        test.getRoot().setId(0);
        test.getRoot().setLeft(new DTreeNode(1));
        test.getRoot().setRight(new DTreeNode(2));
        test.getRoot().getLeft().setLeft(new DTreeNode(3));
        test.getRoot().getLeft().setRight(new DTreeNode(4));
        test.getRoot().getRight().setLeft(new DTreeNode(5));
        //create sufficient number of node to test reallocation of memory
        test.getRoot().getRight().setRight(new DTreeNode(6));
        test.getRoot().getRight().getRight().setRight(new DTreeNode(7));

        assertEquals(4, test.numLeaves());
        assertEquals(8, test.numNodes());
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
            subject.printTree(out, subject.getRoot());
            out.flush();
            out.close();
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
    }

}
