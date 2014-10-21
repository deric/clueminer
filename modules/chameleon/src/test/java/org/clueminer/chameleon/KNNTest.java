package org.clueminer.chameleon;

import java.io.IOException;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.PartitioningClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.fixtures.CommonFixture;
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
    public void testFindNeighbours() throws IOException {
        CommonFixture tf = new CommonFixture();
        Dataset data = new SampleDataset();
        DistanceMeasure distanceMeasure = new EuclideanDistance();
        data.attributeBuilder().create("sepal length", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("sepal width", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("petal length", BasicAttrType.NUMERICAL);
        data.attributeBuilder().create("petal width", BasicAttrType.NUMERICAL);
        FileHandler.loadDataset(tf.irisData(), data, 4, ",");
        //Dataset loads twice - why?
        int k = 5;
        KNN knn = new KNN(k);
        int[][] a = knn.findNeighbours(data);
        for (int i = 0; i < data.size(); i++) {
            System.out.print("Row " + i + ": ");
            for (int j = 0; j < k; j++) {
                System.out.print(", " + distanceMeasure.measure(data.instance(i), data.instance(a[i][j])));
                if (j > 0) {
                    assertEquals(true, distanceMeasure.measure(data.instance(i), data.instance(a[i][j])) >= distanceMeasure.measure(data.instance(i), data.instance(a[i][j-1])));
                }
                
            }
            System.out.println();
        }
    }

}
