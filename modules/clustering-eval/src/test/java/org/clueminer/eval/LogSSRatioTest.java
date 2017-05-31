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
package org.clueminer.eval;

import org.clueminer.clustering.api.ScoreException;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class LogSSRatioTest {

    private final LogSSRatio subject;
    private static final double DELTA = 1e-9;

    public LogSSRatioTest() {
        subject = new LogSSRatio();
    }

    @Test
    public void testIris() throws ScoreException {
        double s1 = subject.score(FakeClustering.iris());
        double s2 = subject.score(FakeClustering.irisMostlyWrong());
        double s3 = subject.score(FakeClustering.irisWrong5());
        assertEquals(true, subject.isBetter(s1, s2));
        assertEquals(true, subject.isBetter(s1, s3));
    }

    @Test
    public void testCompareScore() {
        assertEquals(false, subject.isBetter(2, 20));
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
        double score = subject.score(FakeClustering.int100p4());
        assertEquals(3.40114842491597, score, DELTA);
    }

}
