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
package org.clueminer.eval.external;

import org.clueminer.clustering.api.ScoreException;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class SpecificityTest extends ExternalTest {

    public SpecificityTest() {
        subject = new Specificity();
    }

    @Test
    public void testOneClassPerCluster() throws ScoreException {
        assertEquals(0.0, subject.score(oneClassPerCluster()), delta);
    }

    @Test
    public void testMostlyWrong() throws ScoreException {
        double score = subject.score(FakeClustering.irisMostlyWrong());
        System.out.println("specificity (mw) = " + score);
        assertEquals(true, score < 0.7);
    }

    @Test
    public void testIrisCorrect() throws ScoreException {
        //this is fixed clustering which correspods to true classes in dataset
        measure(FakeClustering.iris(), 1.0);
    }
}
