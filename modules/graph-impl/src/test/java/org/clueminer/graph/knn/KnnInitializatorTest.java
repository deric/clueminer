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
package org.clueminer.graph.knn;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.adjacencyList.AdjListFactory;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphConvertor;
import org.clueminer.graph.api.GraphConvertorFactory;
import org.clueminer.report.NanoBench;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 * Benchmark all k-nng creators
 *
 * @author deric
 */
public class KnnInitializatorTest {

    @Test
    public void testKNNG() {
        Props params = new Props();
        measure(FakeDatasets.irisDataset(), params);
        measure(FakeDatasets.vehicleDataset(), params);
    }

    private void benchmark(final Dataset<? extends Instance> dataset, final GraphConvertor gc, final Props params) {
        //measure clustering run
        NanoBench.create().measurements(3).measure(
                dataset.getName() + " - " + gc.getName(), new Runnable() {
            @Override
            public void run() {
                Graph graph = new AdjListGraph();
                Long[] mapping = AdjListFactory.getInstance().createNodesFromInput(dataset, graph);
                gc.createEdges(graph, dataset, mapping, params);
                assertEquals(dataset.size(), graph.getNodeCount());
            }
        });
        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void measure(Dataset<? extends Instance> dataset, Props params) {
        GraphConvertorFactory gcf = GraphConvertorFactory.getInstance();
        for (GraphConvertor gc : gcf.getAll()) {
            benchmark(dataset, gc, params);
        }
    }

}
