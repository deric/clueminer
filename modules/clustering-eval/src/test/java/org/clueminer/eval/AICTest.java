/*
 * Copyright (C) 2011-2019 clueminer.org
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

import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class AICTest {

    private final AIC subject;
    private static final double DELTA = 1e-9;

    public AICTest() {
        subject = new AIC();
    }

    @Test
    public void testIris() throws ScoreException {
        double scoreBetter = subject.score(FakeClustering.iris());
        double scoreWorser = subject.score(FakeClustering.irisWrong());
        //should recognize better clustering
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
    }

    @Test
    public void testIrisCorrect() throws ScoreException {
        Clustering clust = FakeClustering.iris();
        //according to paper: -132.829
        assertEquals(131.1674232004384, subject.score(clust), DELTA);
        //according to paper: -1653.895
        assertEquals(-1655.5588166451687, subject.clusteringLoglikehood(clust, 4), DELTA);

        Clustering c2 = FakeClustering.irisTwoClusters();
        //according to paper: -1463.770
        assertEquals(-1464.9698169654102, subject.clusteringLoglikehood(c2, 4), DELTA);
    }

    @Test
    public void testInf() throws ScoreException {
        Clustering clust = FakeClustering.irisMostlyWrong();
        assertEquals(true, !Double.isNaN(subject.score(clust)));

        double scoreBetter = subject.score(FakeClustering.iris());
        double scoreWorser = subject.score(FakeClustering.irisMostlyWrong());
        //should recognize better clustering
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
    }

}
