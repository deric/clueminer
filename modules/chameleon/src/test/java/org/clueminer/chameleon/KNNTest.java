package org.clueminer.chameleon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.PartitioningClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixEdge;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixFactory;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixNode;
import org.clueminer.io.FileHandler;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class KNNTest {

    @Test
    public void irisDataTest() throws IOException {
        CommonFixture tf = new CommonFixture();
        Dataset data = new SampleDataset();
        DistanceMeasure distanceMeasure = new EuclideanDistance();
        data.attributeBuilder().create("sepal length", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("sepal width", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("petal length", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("petal width", BasicAttrType.NUMERICAL);
        FileHandler.loadDataset(tf.irisData(), data, 4, ",");

        int k = 5;
        KNN knn = new KNN(k);
        int[][] a = knn.getNeighborArray(data);
        for (int i = 0; i < data.size(); i++) {
          //  System.out.print("Row " + i + ": ");
            for (int j = 0; j < k; j++) {
            //    System.out.print(", " + distanceMeasure.measure(data.instance(i), data.instance(a[i][j])));
                if (j > 0) {
                    assertEquals(true, distanceMeasure.measure(data.instance(i), data.instance(a[i][j])) >= distanceMeasure.measure(data.instance(i), data.instance(a[i][j - 1])));
                }

            }
           // System.out.println();
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
        DistanceMeasure dm = new EuclideanDistance();
        KNN knn = new KNN(4);
        int[][] a = knn.getNeighborArray(dataset);

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
    public void printGraph() {
        Dataset<? extends Instance> dataset = simpleData();
        DistanceMeasure dm = new EuclideanDistance();
        KNN knn = new KNN(4);
        int[][] a = knn.getNeighborArray(dataset);
        
        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);
        System.out.println(g.graphVizExport());
    }

}
