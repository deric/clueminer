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
package org.clueminer.graph.adjacencyMatrix;

import java.util.HashSet;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.api.Graph;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AdjMatrixFactoryTest<E extends Instance> {

    private final AdjMatrixFactory subject;

    public AdjMatrixFactoryTest() {
        subject = new AdjMatrixFactory();
    }

    @Test
    public void testNoise() {
        Dataset<E> data = (Dataset<E>) FakeDatasets.irisDataset();
        Graph<E> graph = new AdjMatrixGraph();
        HashSet<Integer> noise = new HashSet<>();
        noise.add(0);
        noise.add(1);
        noise.add(9);
        noise.add(42);

        Long[] mapping = subject.createNodesFromInput(data, graph, noise);
        assertEquals(data.size(), mapping.length);
        //should not be mapped to anything
        assertEquals(null, mapping[0]);

    }
}
