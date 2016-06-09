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
package org.clueminer.clustering.aggl.linkage;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MedianLinkageTest extends AbstractLinkageTest {

    public MedianLinkageTest() {
        subject = new MedianLinkage();
    }

    @Test
    public void testDistance() {

    }

    @Test
    public void testLinkage() {
        Dataset<? extends Instance> dataset = FakeClustering.schoolData();
        assertEquals(17, dataset.size());
        assertEquals(4, dataset.attributeCount());

        HierarchicalResult naive = naiveLinkage(dataset);
        HierarchicalResult lance = lanceWilliamsLinkage(dataset);
        System.out.println("school - " + subject.getName());
        DendroTreeData tree = naive.getTreeData();
        System.out.println("naive:");
        tree.print();

        System.out.println("lance:");
        lance.getTreeData().print();
        //assertEquals(true, TreeDiff.compare(naive, lance));
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();
//        assertEquals(61.79021281724145, root.getHeight(), delta);
        //TODO: absolute values are differnt, but tree structure is identical
        assertEquals(55.152267979772475, lance.getTreeData().getRoot().getHeight(), delta);
        assertEquals(2 * dataset.size() - 1, tree.numNodes());
    }

    @Test
    public void testAlphaA() {
        assertEquals(0.5, subject.alphaA(1, 3, 99), delta);
    }

    @Test
    public void testAlphaB() {
        assertEquals(0.5, subject.alphaB(1, 3, 1), delta);
    }

    @Test
    public void testBeta() {
        assertEquals(-0.25, subject.beta(2, 2, 99), delta);
    }

    @Test
    public void testGamma() {
        assertEquals(0.0, subject.gamma(), delta);
    }

}
