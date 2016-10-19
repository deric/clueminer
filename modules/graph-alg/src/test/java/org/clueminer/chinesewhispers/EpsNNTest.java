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
package org.clueminer.chinesewhispers;

import static org.clueminer.chinesewhispers.ChineseWhispers.EDGE_THRESHOLD;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 * @param <E>
 */
public class EpsNNTest<E extends Instance> {

    private final EpsNN subject;

    public EpsNNTest() {
        subject = new EpsNN();
        subject.setDistanceMeasure(EuclideanDistance.getInstance());
    }

    @Test
    public void testIris() {
        Dataset<E> data = (Dataset<E>) FakeDatasets.irisDataset();
        Props params = new Props();
        params.putDouble(EDGE_THRESHOLD, 4);
        Graph g = new AdjListGraph(data.size());

        subject.buildGraph(g, data, params);

        assertEquals(data.size(), g.getNodeCount());
        assertEquals(8640, g.getEdgeCount());
    }

}
