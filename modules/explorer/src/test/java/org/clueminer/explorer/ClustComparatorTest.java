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
package org.clueminer.explorer;

import org.clueminer.eval.AIC;
import org.clueminer.eval.Silhouette;
import org.clueminer.eval.external.NMIavg;
import org.clueminer.eval.external.NMIsqrt;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openide.nodes.Node;

/**
 *
 * @author deric
 */
public class ClustComparatorTest {

    private final ClustComparator subject = new ClustComparator(new NMIavg());

    //inverse ordering (descending)
    @Test
    public void testCompare() {
        Node n1 = new ClusteringNode(FakeClustering.iris());
        Node n2 = new ClusteringNode(FakeClustering.irisMostlyWrong());
        //first one is "better"
        assertEquals(-1, subject.compare(n1, n2));
        //first one is worser
        assertEquals(1, subject.compare(n2, n1));

        //the very same clusterings
        assertEquals(0, subject.compare(n1, n1));
    }

    @Test
    public void testSetEvaluator() {
        subject.setEvaluator(new Silhouette());
        Node a = new ClusteringNode(FakeClustering.irisWrong2());
        Node b = new ClusteringNode(FakeClustering.irisWrong4());
        Node c = new ClusteringNode(FakeClustering.iris());

        //first one is "better" DESC, A > B
        assertEquals(-1, subject.compare(a, b));
        //C > B
        assertEquals(-1, subject.compare(c, b));
        assertEquals(-1, subject.compare(c, a));
    }

    @Test
    public void testMaximize() {
        subject.setEvaluator(new NMIsqrt());
        Node a = new ClusteringNode(FakeClustering.iris());
        Node b = new ClusteringNode(FakeClustering.iris());
        Node c = new ClusteringNode(FakeClustering.irisMostlyWrong());
        assertEquals(0, subject.compare(a, b));

        assertEquals(1, subject.compare(c, a));
    }

    /**
     * Objective that is supposed to be minimized
     */
    @Test
    public void testMinimize() {
        subject.setEvaluator(new AIC());
        Node a = new ClusteringNode(FakeClustering.iris());
        Node b = new ClusteringNode(FakeClustering.iris());
        Node c = new ClusteringNode(FakeClustering.irisMostlyWrong());
        assertEquals(0, subject.compare(a, b));

        assertEquals(1, subject.compare(c, a));
    }

}
