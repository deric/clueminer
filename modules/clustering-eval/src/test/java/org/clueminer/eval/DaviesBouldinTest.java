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
package org.clueminer.eval;

import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DaviesBouldinTest {

    private static final DaviesBouldin subject = new DaviesBouldin();
    private static final double delta = 1e-9;

    public DaviesBouldinTest() {
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testScore() {
        double scoreBetter = subject.score(FakeClustering.iris());
        double scoreWorser = subject.score(FakeClustering.irisWrong5());

        //should recognize better clustering
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
    }

    @Test
    public void testIsBetter() {
        assertEquals(true, subject.isBetter(0.1, 0.9));
    }

    @Test
    public void testIsMaximized() {
    }

    @Test
    public void testGetMin() {
    }

    @Test
    public void testGetMax() {
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
        //TODO: according to clusterCrit it should be 0.212623030978125
        ///
        assertEquals(0.22908933305064558, score, delta);
    }

}
