/*
 * Copyright (C) 2011-2015 clueminer.org
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CompactnessTest {

    private final Compactness subject;
    private final double delta = 1e-9;

    public CompactnessTest() {
        subject = new Compactness();
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testIris() {
        double scoreBetter = subject.score(FakeClustering.iris());
        assertEquals(0.9574211390878558, scoreBetter, delta);
        double scoreWorser = subject.score(FakeClustering.irisMostlyWrong());

        System.out.println("better: " + scoreBetter);
        System.out.println("worser: " + scoreWorser);

        //should recognize better clustering
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
    }

    @Test
    public void testScore() {
    }

    @Test
    public void testIsBetter() {
    }

    @Test
    public void testIsMaximized() {
    }

    @Test
    public void testCompareScore() {
        double scoreBetter = subject.score(FakeClustering.iris());
        double scoreWorser = subject.score(FakeClustering.irisMostlyWrong());

        //should recognize better clustering
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
    }

}
