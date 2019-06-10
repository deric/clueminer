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
package org.clueminer.eval.external;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class VITest extends ExternalTest {

    private static Clustering irisCorrect;
    private static Clustering irisWrong;

    public VITest() throws FileNotFoundException, IOException {
        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong2();
        subject = new VI();
    }

    @Test
    public void testScore_Clustering_Clustering() throws ScoreException {
        //this is fixed clustering which correspods to true classes in dataset
        measure(FakeClustering.iris(), FakeClustering.iris(), 0.0);

        double score = measure(irisCorrect, irisWrong, -0.6864353427097161);

        double score2 = measure(irisCorrect, FakeClustering.irisWrong(), -1.252502130361933);
        assertTrue(score2 < score);
    }

    @Test
    public void testScore_Clustering_Dataset() throws ScoreException {
        measure(FakeClustering.irisTwoClusters(), -0.4620981203732968);
        measure(FakeClustering.irisMostlyWrong(), -1.1239230673308311);

        double score = measure(irisWrong, -0.6864353427097156);
        measure(irisCorrect, FakeClustering.irisWrong(), -1.252502130361933);

        assertTrue(subject.isBetter(0.0, score));
    }

    @Test
    public void testCompareScore() {
        assertTrue(subject.isBetter(0.0, -1.0));
        assertTrue(subject.isBetter(-0.5, -1.0));
        assertTrue(subject.isBetter(-0.9999, -1.0));
    }

    @Test
    public void testIdeal() throws ScoreException {
        assertEquals(0.0, subject.score(irisCorrect), DELTA);
    }

}
