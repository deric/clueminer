package org.clueminer.chinesewhispers;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class ChineseWhispersTest {

    private Dataset<? extends Instance> generateData() {
        double[][] data = new double[][]{
            /* n0 n1 n2 n3 n4 n5 n6 */
            /* n0 */{0, 1, 1, 0, 0, 0, 0},
            /* n1 */ {1, 0, 0, 0, 0, 0, 0},
            /* n2 */ {1, 0, 0, 0, 0, 0, 0},
            /* n3 */ {0, 0, 0, 0, 1, 0, 0},
            /* n4 */ {0, 0, 0, 0, 0, 1, 1},
            /* n5 */ {0, 0, 0, 0, 1, 0, 0},
            /* n6 */ {0, 0, 0, 0, 1, 0, 0}
        };
        Dataset<Instance> dataset = new ArrayDataset<>(data);
        return dataset;
    }

    @Test
    public void getNameTest() {
        System.out.println("Get Name Test");
        ChineseWhispers cw = new ChineseWhispers();
        assertEquals("Chinese Whispers", cw.getName());
    }

    @Test
    public void clusterTest() {
        System.out.println("Read Data Test");
        ChineseWhispers cw = new ChineseWhispers();
        Dataset<? extends Instance> data = generateData();
        Props p = new Props();
        cw.cluster(data, p);
    }
}
