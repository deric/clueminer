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
package org.clueminer.meta.features;

import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.clueminer.meta.features.DsBaseTest.stat;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DsMedianSkewnessTest extends DsBaseTest {

    @BeforeClass
    public static void setUpClass() {
        stat = new DsMedianSkewness();
    }

    @Test
    public void testEvaluate() {
        double v = stat.evaluate(FakeDatasets.irisDataset(), DsMedianSkewness.MED_SKEW, null);
        assertEquals(-0.21266386350078934, v, DELTA);
    }

}
