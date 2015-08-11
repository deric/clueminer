/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.eval;

import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class BanfieldRafteryTest extends InternalTest {

    private final BanfieldRaftery subject;
    private static final double delta = 1e-9;

    public BanfieldRafteryTest() {
        subject = new BanfieldRaftery();
    }

    @Test
    public void testIris() {
        double s1 = subject.score(FakeClustering.iris());
        double s2 = subject.score(FakeClustering.irisMostlyWrong());
        double s3 = subject.score(FakeClustering.irisWrong5());
        assertEquals(true, subject.isBetter(s1, s2));
        assertEquals(true, subject.isBetter(s1, s3));
    }

    @Test
    public void testCompareScore() {
        assertEquals(true, subject.isBetter(2, 20));
    }

    @Test
    public void testOneClassPerCluster() {
        assertEquals(0.0, subject.score(oneClassPerCluster()), delta);
    }

    /**
     * Check against definition (and tests in R package clusterCrit)
     * https://cran.r-project.org/web/packages/clusterCrit/index.html
     *
     * NOTE: There's a small problem with precision of floating point
     * operations. First 7 decimal digits seems to match.
     */
    @Test
    public void testClusterCrit() {
        double score = subject.score(FakeClustering.int100p4());
        //clusterCrit = -534.545225046529
        assertEquals(-534.545225046529, score, delta);
    }

}
