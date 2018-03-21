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
import org.junit.Test;

/**
 *
 * @author tombart
 */
public class AccuracyTest extends ExternalTest {

    public AccuracyTest() {
        subject = new Accuracy();
    }

    /**
     * Test of score method, of class Accuracy.
     */
    @Test
    public void testScore_Clustering_Clustering() throws ScoreException {
        double score;
        //each cluster should have this scores:
        //Cabernet = 0.7407
        //Syrah = 0.7037
        //Pinot = 0.8889
        score = measure(FakeClustering.wineClustering(), FakeClustering.wineCorrect(), 0.6438746438746439);

        //when using class labels result should be the same
        measure(FakeClustering.wineClustering(), score);
    }

    @Test
    public void testOneClassPerCluster() throws ScoreException {
        assertEquals(0.0, subject.score(oneClassPerCluster()), delta);
    }

    /**
     * Test of isBetter method, of class Accuracy.
     */
    @Test
    public void testCompareScore() throws ScoreException {
        double scoreBetter = subject.score(FakeClustering.iris());
        double scoreWorser = subject.score(FakeClustering.irisWrong5());

        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
    }

    @Test
    public void testMostlyWrong() throws ScoreException {
        double score = subject.score(FakeClustering.irisMostlyWrong());
        System.out.println("accuracy (mw) = " + score);
        assertEquals(true, score < 0.4);
    }
}
