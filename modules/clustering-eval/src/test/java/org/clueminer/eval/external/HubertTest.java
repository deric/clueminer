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
package org.clueminer.eval.external;

import org.clueminer.clustering.api.ScoreException;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.clueminer.eval.external.ExternalTest.DELTA;

/**
 *
 * @author deric
 */
public class HubertTest extends ExternalTest {

    public HubertTest() {
        subject = new Hubert();
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    /**
     * Test of score method, of class FowlkesMallows.
     */
    @Test
    public void testScore_Clustering_Dataset() throws ScoreException {
        //this is fixed clustering which correspods to true classes in dataset
        measure(FakeClustering.iris(), 1.0);
        measure(FakeClustering.irisWrong2(), 0.4729957505755127);
    }

    @Test
    public void testCompareScore() {
        //bigger is better
        assertTrue(subject.isBetter(0.555, 0.12));
    }

    @Test
    public void testScore_Clustering_Clustering() throws ScoreException {
        double score;
        score = measure(FakeClustering.wineClustering(), FakeClustering.wineCorrect(), 0.21073236216545863);

        //when using class labels result should be the same
        measure(FakeClustering.wineClustering(), score);
    }

    @Test
    public void testOneClassPerCluster() throws ScoreException {
        assertEquals(Double.NaN, subject.score(oneClassPerCluster()), DELTA);
    }

    @Test
    public void testMostlyWrong() throws ScoreException {
        double score = subject.score(FakeClustering.irisMostlyWrong());
        assertEquals(0.0, score, DELTA);
    }

    /**
     * Check against definition (and tests in R package clusterCrit)
     * https://cran.r-project.org/web/packages/clusterCrit/index.html
     *
     * NOTE: There's a small problem with precision of floating point
     * operations. First 7 decimal digits seems to match.
     */
    @Test
    public void testClusterCrit() throws ScoreException {
        measure(ext100p2, ext100p3, 0.022714141794819);
    }
}
