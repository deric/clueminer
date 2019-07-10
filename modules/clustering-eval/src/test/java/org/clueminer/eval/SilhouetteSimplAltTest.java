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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class SilhouetteSimplAltTest {

    private final SilhouetteSimplAlt subject;
    private static final double DELTA = 1e-9;

    public SilhouetteSimplAltTest() {
        subject = new SilhouetteSimplAlt();
    }

    @Test
    public void testIris() throws ScoreException {
        double scoreBetter = subject.score(FakeClustering.iris());
        assertEquals(5.330092804682181, scoreBetter, DELTA);
        double scoreWorser = subject.score(FakeClustering.irisWrong());
        //should recognize better clustering
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
    }

    @Test
    public void testIsBetter() {
        assertEquals(true, subject.isBetter(0.9, 0.5));
        assertEquals(false, subject.isBetter(0.01, 0.1));
    }

    @Test
    public void testCompare() {
        assertEquals(1, subject.compare(1.0, 0.3));
        assertEquals(-1, subject.compare(0.12, 0.35));
        assertEquals(0, subject.compare(0.132, 0.132));
    }

    @Test
    public void testIsMaximized() {
        assertEquals(true, subject.isMaximized());
    }

}
