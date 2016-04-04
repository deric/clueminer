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
package org.clueminer.eval.external;

import static org.clueminer.eval.external.ExternalTest.delta;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CzekanowskiDiceTest extends ExternalTest {

    public CzekanowskiDiceTest() {
        subject = new CzekanowskiDice();
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testScore_Clustering_Dataset() {
        //this is fixed clustering which correspods to true classes in dataset
        measure(FakeClustering.iris(), 1.0);
        measure(FakeClustering.irisWrong2(), 0.6648648648648648);
    }

    @Test
    public void testCompareScore() {
        //bigger is better
        assertTrue(subject.isBetter(0.555, 0.12));
    }

    @Test
    public void testOneClassPerCluster() {
        assertEquals(0.0, subject.score(oneClassPerCluster()), delta);
    }

    @Test
    public void testMostlyWrong() {
        double score = subject.score(FakeClustering.irisMostlyWrong());
        assertEquals(0.49329977552547444, score, delta);
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
        measure(ext100p2, ext100p3, 0.411693066358566);
    }
}
