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
package edu.umn.metis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.knn.KNNGraphBuilder;
import org.clueminer.utils.Props;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HMetisBisectorTest extends PartitioningTest {

    String output = "path";

    @Test
    public void basicTest() throws IOException, UnsupportedEncodingException, FileNotFoundException, InterruptedException {
        Dataset<? extends Instance> dataset = KLFail();
        KNNGraphBuilder knn = new KNNGraphBuilder();

        Graph g = new AdjMatrixGraph(dataset.size());
        g = knn.getNeighborGraph(dataset, g, 4);

        /*GraphPrinter gp = new GraphPrinter(true);
         gp.printGraph(g, 1, output, "knn");*/
        subject = new HMetisBisector();
        ArrayList<ArrayList<Node>> result = subject.bisect(g, new Props());


        /*        assertEquals(4, result.get(1).get(0).getInstance().getIndex());
         assertEquals(6, result.get(1).get(1).getInstance().getIndex());
         assertEquals(7, result.get(1).get(2).getInstance().getIndex());
         assertEquals(8, result.get(1).get(3).getInstance().getIndex());
         assertEquals(9, result.get(1).get(4).getInstance().getIndex());
         assertEquals(10, result.get(1).get(5).getInstance().getIndex());*/
        //gp.printClusters(g, 1, result, output, "BisectedByFMclusters");
        //g = (AdjMatrixGraph) fm.removeUnusedEdges();
        // gp.printGraph(g, 1, output, "BisectedByFM");
    }

}
