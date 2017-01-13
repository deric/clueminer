/*
 * Copyright (C) 2011-2017 clueminer.org
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

import org.clueminer.eval.Silhouette;
import org.clueminer.eval.external.NMIsum;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openide.nodes.Node;

/**
 *
 * @author deric
 */
public class ClustComparatorTest {

    private final ClustComparator subject = new ClustComparator(new NMIsum());

    @Test
    public void testCompare() {
        Node n1 = new ClusteringNode(FakeClustering.iris());
        Node n2 = new ClusteringNode(FakeClustering.irisWrong());
        subject.setAscOrder(true);
        //first one is "better"
        assertEquals(1, subject.compare(n1, n2));

        subject.setAscOrder(false);
        //first one is "better" (descending order)
        assertEquals(-1, subject.compare(n1, n2));

        //the very same clusterings
        assertEquals(0, subject.compare(n1, n1));
    }

    @Test
    public void testSetEvaluator() {
        subject.setEvaluator(new Silhouette());
        subject.setAscOrder(true);
        Node a = new ClusteringNode(FakeClustering.irisWrong2());
        Node b = new ClusteringNode(FakeClustering.irisWrong4());
        Node c = new ClusteringNode(FakeClustering.iris());

        //first one is "better" DESC, A > B
        assertEquals(1, subject.compare(a, b));
        //C > B
        assertEquals(1, subject.compare(c, b));
        assertEquals(1, subject.compare(c, a));
    }

    public void testNaN() {
        assertEquals(true, 0.1 > Double.NaN);
        assertEquals(false, Double.NaN > Double.NaN);
        assertEquals(true, Double.NaN == Double.NaN);
    }

}
