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
package org.clueminer.bio;

import org.clueminer.dataset.api.StatsNum;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class GenomeSequenceTest {

    private GenomeSequence subject;
    private static final double DELTA = 1e-9;

    public GenomeSequenceTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testGenom() {
        subject = new GenomeSequence(4, 4);
        subject.setObject(0, "A");
        subject.setObject(1, "C");
        subject.setObject(2, "G");
        subject.setObject(3, "T");

        assertEquals(4, subject.size());
        assertEquals(0.0, subject.statistics(StatsNum.SUM), DELTA);
    }

    /**
     * A-T pair and C-G pair should sum to zero.
     */
    @Test
    public void testChar() {
        subject = new GenomeSequence(2, 4);
        subject.setObject(0, 'A');
        subject.setObject(1, 'T');

        assertEquals(2, subject.size());
        assertEquals(0.0, subject.statistics(StatsNum.SUM), DELTA);

        subject = new GenomeSequence(2, 4);
        subject.setObject(0, 'G');
        subject.setObject(1, 'C');

        assertEquals(2, subject.size());
        assertEquals(0.0, subject.statistics(StatsNum.SUM), DELTA);
    }

}
