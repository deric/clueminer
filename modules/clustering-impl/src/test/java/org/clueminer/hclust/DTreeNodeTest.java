/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.hclust;

import org.clueminer.clustering.api.dendrogram.DendroNode;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DTreeNodeTest {

    private DendroNode subject;
    private static DendroNode tree;
    private static final double DELTA = 1e-9;
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

    @Before
    public void setUp() {
        subject = new DTreeNode();
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

    @Test
    public void testGetParent() {
        assertEquals(null, subject.getParent());
    }

    /**
     * Test of getHeight method, of class DTreeNode.
     */
    @Test
    public void testGetHeight() {
        assertEquals(0.0, subject.getHeight(), DELTA);
    }


}
