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
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author deric
 */
public class AICTest {

    private final AIC subject;

    public AICTest() {
        subject = new AIC();
    }

    @Test
    public void testIris() {
        double scoreBetter = subject.score(FakeClustering.iris());
        double scoreWorser = subject.score(FakeClustering.irisMostlyWrong());

        //should recognize better clustering
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
    }

    @Test
    public void testIsBetter() {
        assertEquals(true, subject.isBetter(-237.847, -201.928));
        assertEquals(false, subject.isBetter(-201.928, -237.847));
    }

    /**
     * @link http://stats.stackexchange.com/questions/84076/negative-values-for-aic-in-general-mixed-model
     */
    @Test
    public void testCompare() {
        assertEquals(-1, subject.compare(-237.847, -201.928));
        assertEquals(1, subject.compare(-201.928, -237.847));
    }

    @Test
    public void testIsMaximized() {
        assertEquals(false, subject.isMaximized());
    }

}
