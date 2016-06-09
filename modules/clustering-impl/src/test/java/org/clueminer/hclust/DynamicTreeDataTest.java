/*
 * Copyright (C) 2011-2016 clueminer.org
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

import java.io.IOException;
import java.io.OutputStreamWriter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class DynamicTreeDataTest {

    private final DynamicTreeData subject;

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

        //3 different node heights
        DynamicTreeData t = new DynamicTreeData();
        t.setRoot(new DTreeNode(true)).setHeight(15);
        t.getRoot().setLeft(new DTreeNode(1)).setHeight(12)
                .setLeft(new DTreeNode(2));
        t.getRoot().setRight(new DTreeNode(3)).setHeight(13)
                .setLeft(new DTreeNode(4));

        t.print();
        assertEquals(3, t.distinctHeights());
        assertEquals(2, t.treeLevels());

        //3 different node heights
        DynamicTreeData t2 = new DynamicTreeData();
        t2.setRoot(new DTreeNode(true)).setHeight(15);
        t2.getRoot().setLeft(new DTreeNode(1)).setHeight(12)
                .setLeft(new DTreeNode(2));
        t2.getRoot().setRight(new DTreeNode(3)).setHeight(12)
                .setLeft(new DTreeNode(4));

        t2.print();
        assertEquals(2, t2.distinctHeights());
        assertEquals(2, t2.treeLevels());
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
        DynamicTreeData dtd = new DynamicTreeData();
        assertEquals(false, dtd.hasMapping());
    }

    @Test
    public void testPrintPaths() {
        subject.printPaths();
    }

    @Test
    public void testPrint() {
        try (OutputStreamWriter out = new OutputStreamWriter(System.out)) {
            subject.printTree(out, subject.getRoot());
            out.flush();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

}
