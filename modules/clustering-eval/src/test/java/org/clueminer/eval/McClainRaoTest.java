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
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class McClainRaoTest {

    private final McClainRao subject;
    private static final double delta = 1e-9;

    public McClainRaoTest() {
        subject = new McClainRao();
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testClusterCrit() throws ScoreException {
        double score = subject.score(FakeClustering.int100p4());
        //clustCrit: 0.141800605341339
        assertEquals(0.14180060534134048, score, delta);
    }
}
