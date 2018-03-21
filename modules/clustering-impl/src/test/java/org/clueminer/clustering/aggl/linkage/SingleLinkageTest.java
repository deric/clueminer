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
package org.clueminer.clustering.aggl.linkage;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.TreeDiff;
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
public class SingleLinkageTest extends AbstractLinkageTest {

    public SingleLinkageTest() {
        subject = new SingleLinkage();
    }

    @Test
    public void testLinkageSchool() {
        Dataset<? extends Instance> dataset = FakeClustering.schoolData();
        assertEquals(17, dataset.size());

        HierarchicalResult naive = naiveLinkage(dataset);
        HierarchicalResult lance = lanceWilliamsLinkage(dataset);
        assertEquals(true, TreeDiff.compare(naive, lance));
        System.out.println("school - " + subject.getName());
        DendroTreeData tree = naive.getTreeData();
        tree.print();
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();
        assertEquals(32.542734980330046, root.getHeight(), delta);
        assertEquals(32.542734980330046, lance.getTreeData().getRoot().getHeight(), delta);
        assertEquals(2 * dataset.size() - 1, tree.numNodes());
    }

    /**
     * TODO: this might fail because of same distance between nodes: 0.14
     */
    //@Test
    public void testLinkageKumar() {
        Dataset<? extends Instance> dataset = FakeClustering.kumarData();

        HierarchicalResult naive = naiveLinkage(dataset);
        HierarchicalResult lance = lanceWilliamsLinkage(dataset);
        assertEquals(true, TreeDiff.compare(naive, lance));
        System.out.println(dataset.getName() + " - " + subject.getName());
        DendroTreeData tree = naive.getTreeData();
        tree.print();
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();
     //   assertEquals(32.542734980330046, root.getHeight(), delta);
        //   assertEquals(32.542734980330046, lance.getTreeData().getRoot().getHeight(), delta);
        assertEquals(2 * dataset.size() - 1, tree.numNodes());
    }

    @Test
    public void testAlphaA() {
        assertEquals(0.5, subject.alphaA(1, 3, 1), delta);
    }

    @Test
    public void testAlphaB() {
        assertEquals(0.5, subject.alphaB(1, 3, 1), delta);
    }

    @Test
    public void testBeta() {
        assertEquals(0.0, subject.beta(1, 2, 3), delta);
    }

    @Test
    public void testGamma() {
        assertEquals(-0.5, subject.gamma(), delta);
    }

}
