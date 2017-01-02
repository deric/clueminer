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

import java.util.HashMap;
import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.clueminer.meta.features.DsBaseTest.stat;
import org.clueminer.utils.Props;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DsGraphTest extends DsBaseTest {

    @BeforeClass
    public static void setUpClass() {
        stat = new DsGraph();
    }

    @Test
    public void testEvaluate() {
        //double v = stat.evaluate(FakeDatasets.irisDataset(), DsGraph.EDGES, null);
        Props params = new Props();
        params.putInt("k", 5);
        params.put("graph_conv", "k-NNG");
        HashMap<String, Double> features = new HashMap<>();
        stat.computeAll(FakeDatasets.irisDataset(), features, params);
        //assertEquals(0.31999051830383357, v, DELTA);
        System.out.println("f: " + features.toString());
    }

}
