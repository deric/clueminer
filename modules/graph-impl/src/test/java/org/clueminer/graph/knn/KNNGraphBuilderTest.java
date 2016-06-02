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

import java.io.IOException;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.dataset.impl.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.GraphStorageFactory;
import org.clueminer.io.FileHandler;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class KNNGraphBuilderTest {

    private final KNNGraphBuilder subject;

    public KNNGraphBuilderTest() {
        subject = new KNNGraphBuilder();
    }

    @Test
    public void irisDataTest() throws IOException {
        CommonFixture tf = new CommonFixture();
        Dataset data = new SampleDataset();
        Distance distanceMeasure = new EuclideanDistance();
        data.attributeBuilder().create("sepal length", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("sepal width", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("petal length", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("petal width", BasicAttrType.NUMERICAL);
        FileHandler.loadDataset(tf.irisData(), data, 4, ",");

        int k = 5;
        int[][] a = subject.getNeighborArray(data, k);
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < k; j++) {
                if (j > 0) {
                    assertEquals(true, distanceMeasure.measure(data.instance(i), data.instance(a[i][j])) >= distanceMeasure.measure(data.instance(i), data.instance(a[i][j - 1])));
                }

            }
        }
    }

    private Dataset<? extends Instance> simpleData() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{0, 0});
        data.builder().create(new double[]{1, 3});
        data.builder().create(new double[]{2, 2});
        data.builder().create(new double[]{2, 1});
        data.builder().create(new double[]{4, 4});
        return data;
    }

    @Test
    public void simpleDataTest() {
        Dataset<? extends Instance> dataset = simpleData();
        Distance dm = new EuclideanDistance();
        subject.setDistanceMeasure(dm);
        int[][] a = subject.getNeighborArray(dataset, 4);

        assertEquals(3, a[0][0]);
        assertEquals(2, a[0][1]);
        assertEquals(1, a[0][2]);
        assertEquals(4, a[0][3]);

        assertEquals(2, a[1][0]);
        assertEquals(3, a[1][1]);
        assertEquals(true, (a[1][2] == 0 || a[1][2] == 4));
        assertEquals(true, (a[1][3] == 0 || a[1][3] == 4));

        assertEquals(3, a[2][0]);
        assertEquals(1, a[2][1]);
        assertEquals(true, (a[2][2] == 0 || a[2][2] == 4));
        assertEquals(true, (a[2][3] == 0 || a[2][3] == 4));

        assertEquals(2, a[3][0]);
        assertEquals(true, (a[3][1] == 0 || a[3][1] == 1));
        assertEquals(true, (a[3][2] == 0 || a[3][2] == 1));
        assertEquals(4, a[3][3]);

        assertEquals(2, a[4][0]);
        assertEquals(1, a[4][1]);
        assertEquals(3, a[4][2]);
        assertEquals(0, a[4][3]);
    }

    @Test
    public void testBuildingGraph() {
        GraphStorageFactory gbf = GraphStorageFactory.getInstance();
        //test different graph builders
        for (Graph gs : gbf.getAll()) {
            constructGraph(gs);
        }
    }

    private void constructGraph(Graph graph) {
        GraphBuilder gb = graph.getFactory();
        System.out.println("running " + gb.getName());
        Dataset<? extends Instance> dataset = FakeDatasets.schoolData();
        Graph g = GraphStorageFactory.getInstance().newInstance(graph.getName());
        g.ensureCapacity(dataset.size());
        assertEquals(0, g.getNodeCount());
        Long[] mapping = gb.createNodesFromInput(dataset, g);
        assertEquals(dataset.size(), mapping.length);
        Props params = new Props();
        params.putInt("k", 2);
        assertEquals(0, g.getEdgeCount());
        for (int i = 0; i < mapping.length; i++) {
            assertNotEquals(null, g.getNode(i));
        }
        subject.createEdges(g, dataset, mapping, params);
        assertEquals(22, g.getEdgeCount());
    }
}
