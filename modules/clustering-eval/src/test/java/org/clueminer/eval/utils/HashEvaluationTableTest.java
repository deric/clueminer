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
package org.clueminer.eval.utils;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.eval.external.Precision;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Dump;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HashEvaluationTableTest {

    private HashEvaluationTable subject;
    private static Clustering irisCorrect;
    private static final double DELTA = 1e-9;

    public HashEvaluationTableTest() {
        subject = new HashEvaluationTable(irisCorrect, FakeDatasets.irisDataset());
    }

    @BeforeClass
    public static void setUpClass() {
        irisCorrect = FakeClustering.iris();
    }

    @Test
    public void testGetScore() {
        double score = subject.getScore(new Precision());
        assertEquals(1.0, score, DELTA);
    }

    @Test
    public void testGetEvaluators() {
        String[] eval = subject.getEvaluators();
        assertEquals(true, eval.length > 0);
        Dump.array(eval, "evaluators");
    }

    @Test
    public void testGetInternal() {
        System.out.println("internal: " + subject.getInternal().toString());
    }

    @Test
    public void testGetExternal() {
        System.out.println("external: " + subject.getExternal().toString());
    }

}
