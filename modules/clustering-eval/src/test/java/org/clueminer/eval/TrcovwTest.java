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
public class TrcovwTest {

    private final Trcovw subject;
    private static final double DELTA = 1e-9;

    public TrcovwTest() {
        subject = new Trcovw();
    }

    @Test
    public void testIris() throws ScoreException {
        double scoreBetter = subject.score(FakeClustering.iris());
        double scoreWorser = subject.score(FakeClustering.irisWrong4());

        //should recognize better clustering
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));

        // value according to R's NbClust package
        assertEquals(357.162304790009, scoreBetter, DELTA);
    }

    @Test
    public void testSpeed1() throws ScoreException {
        long start = System.currentTimeMillis();
        double sc1 = subject.score(FakeClustering.iris());
        long end = System.currentTimeMillis();
        System.out.println("sc1 took " + (end - start) + " ms, score " + sc1);
    }

    @Test
    public void testSpeed2() throws ScoreException {
        long start = System.currentTimeMillis();
        double sc2 = subject.score2(FakeClustering.iris());
        long end = System.currentTimeMillis();
        System.out.println("sc2 took " + (end - start) + " ms, score " + sc2);
    }

}
