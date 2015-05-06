package org.clueminer.graph.GraphBuilder;

import java.io.IOException;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.FileHandler;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class KNNGraphBuilderTest {

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
        KNNGraphBuilder knn = new KNNGraphBuilder();
        int[][] a = knn.getNeighborArray(data, k);
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
        DistanceMeasure dm = new EuclideanDistance();
        KNNGraphBuilder knn = new KNNGraphBuilder();
        int[][] a = knn.getNeighborArray(dataset, 4);

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
}
