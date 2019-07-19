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

import org.clueminer.clustering.api.ScoreException;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class RubinTest {

    private final Rubin subject;
    private static final double DELTA = 1e-9;

    public RubinTest() {
        subject = new Rubin();
    }

    @Test
    public void testIris() throws ScoreException {
        double scoreBetter = subject.score(FakeClustering.iris());
        double scoreWorser = subject.score(FakeClustering.irisWrong5());

        //should recognize better clustering
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
        // value according to R's NbClust package
        // appears to be using uncentered column vectors
        // assertEquals(106.826066604402, scoreBetter, DELTA);
        double sc2 = subject.scoreOld(FakeClustering.iris(), new Props());
        assertEquals(sc2, scoreBetter, DELTA);
    }

    @Test
    public void testSpeed1() throws ScoreException {
        long start = System.currentTimeMillis();
        double sc1 = subject.score(FakeClustering.wineCorrect());
        long end = System.currentTimeMillis();
        System.out.println("rubin took " + (end - start) + " ms, score " + sc1);
    }

    @Test
    public void testSpeed2() throws ScoreException {
        long start = System.currentTimeMillis();
        double sc2 = subject.scoreOld(FakeClustering.wineCorrect(), new Props());
        long end = System.currentTimeMillis();
        System.out.println("rubin-matrix took " + (end - start) + " ms, score " + sc2);
    }

}
