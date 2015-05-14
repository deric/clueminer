package org.clueminer.fastcommunity;

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
public class FastCommunityTest {

    private Dataset<? extends Instance> generateData() {
        double[][] data = new double[][]{
            /* n0 n1 n2 n3 */
            //			/* n0 */ {0, 1, 1, 0},
            //			/* n1 */ {1, 0, 1, 0},
            //			/* n2 */ {1, 1, 0, 1},
            //			/* n3 */ {0, 0, 1, 0}
            /* n0 n1 n2 n3 n4 n5 n6 n7 */
            //			/* n0 */ {0, 1, 1, 1, 0, 0, 0, 0},
            //			/* n1 */ {1, 0, 1, 1, 0, 0, 0, 0},
            //			/* n2 */ {1, 1, 0, 1, 0, 0, 0, 0},
            //			/* n3 */ {1, 1, 1, 0, 0, 0, 0, 0},
            //			/* n4 */ {0, 0, 0, 0, 0, 1, 1, 0},
            //			/* n5 */ {0, 0, 0, 0, 1, 0, 1, 0},
            //			/* n6 */ {0, 0, 0, 0, 1, 1, 0, 1},
            //			/* n7 */ {0, 0, 0, 0, 0, 0, 1, 0}
            /* n0 n1 n2 n3 n4 n5 n6 n7  */
            //			/* n0 */ {0, 1, 1, 1, 0, 0, 0, 0},
            //			/* n1 */ {1, 0, 1, 1, 0, 0, 0, 0},
            //			/* n2 */ {1, 1, 0, 1, 0, 0, 0, 0},
            //			/* n3 */ {1, 1, 1, 0, 0, 0, 0, 0},
            //			/* n4 */ {0, 0, 0, 0, 0, 1, 1, 0},
            //			/* n5 */ {0, 0, 0, 0, 1, 0, 1, 0},
            //			/* n6 */ {0, 0, 0, 0, 1, 1, 0, 1},
            //			/* n7 */ {0, 0, 0, 0, 0, 0, 1, 0}
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
            {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0},
            {1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1},
            {0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 1},
            {0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0}
        };
//		int[][] karate = new int[][]{{1,2},
//{1,3},
//{1,4},
//{1,5},
//{1,6},
//{1,7},
//{1,8},
//{1,9},
//{1,11},
//{1,12},
//{1,13},
//{1,14},
//{1,18},
//{1,20},
//{1,22},
//{1,32},
//{2,1},
//{2,3},
//{2,4},
//{2,8},
//{2,14},
//{2,18},
//{2,20},
//{2,22},
//{2,31},
//{3,1},
//{3,2},
//{3,4},
//{3,8},
//{3,9},
//{3,10},
//{3,14},
//{3,28},
//{3,29},
//{3,33},
//{4,1},
//{4,2},
//{4,3},
//{4,8},
//{4,13},
//{4,14},
//{5,1},
//{5,7},
//{5,11},
//{6,1},
//{6,7},
//{6,11},
//{6,17},
//{7,1},
//{7,5},
//{7,6},
//{7,17},
//{8,1},
//{8,2},
//{8,3},
//{8,4},
//{9,1},
//{9,3},
//{9,31},
//{9,33},
//{9,34},
//{10,3},
//{10,34},
//{11,1},
//{11,5},
//{11,6},
//{12,1},
//{13,1},
//{13,4},
//{14,1},
//{14,2},
//{14,3},
//{14,4},
//{14,34},
//{15,33},
//{15,34},
//{16,33},
//{16,34},
//{17,6},
//{17,7},
//{18,1},
//{18,2},
//{19,33},
//{19,34},
//{20,1},
//{20,2},
//{20,34},
//{21,33},
//{21,34},
//{22,1},
//{22,2},
//{23,33},
//{24,26},
//{24,28},
//{24,30},
//{24,33},
//{24,34},
//{25,26},
//{25,28},
//{25,32},
//{26,24},
//{26,25},
//{26,32},
//{27,30},
//{27,34},
//{28,3},
//{28,24},
//{28,25},
//{28,34},
//{29,3},
//{29,32},
//{29,34},
//{30,24},
//{30,27},
//{30,33},
//{30,34},
//{31,2},
//{31,9},
//{31,33},
//{31,34},
//{32,1},
//{32,25},
//{32,26},
//{32,29},
//{32,33},
//{32,34},
//{33,3},
//{33,9},
//{33,15},
//{33,16},
//{33,19},
//{33,21},
//{33,23},
//{33,24},
//{33,30},
//{33,31},
//{33,32},
//{33,34},
//{34,9},
//{34,10},
//{34,14},
//{34,15},
//{34,16},
//{34,19},
//{34,20},
//{34,21},
//{34,23},
//{34,24},
//{34,27},
//{34,28},
//{34,29},
//{34,30},
//{34,31},
//{34,32},
//{34,33}
//};
//		int[][] matrix = new int[34][34];
//		for(int i = 0; i < 34; i++)
//			for(int j = 0; j < 34; j++)
//				matrix[i][j] = 0;
//		for(int i = 0; i < karate.length; i++) {
//			int a = karate[i][0];
//			int b = karate[i][1];
//			matrix[a-1][b-1] = 1;
//			matrix[b-1][a-1] = 1;
//		}
//		for(int i = 0; i < matrix.length; i++) {
//			System.out.print("{");
//			for(int j = 0; j < matrix.length; j++) {
//				System.out.print(matrix[i][j] + ",");
//			}
//			System.out.println("},");
//		}
        Dataset<Instance> dataset = new ArrayDataset<>(data);
        return dataset;
    }

    @Test
    public void clusterTest() {
        System.out.println("Hierarchy Test");
        FastCommunity fc = new FastCommunity();
        Dataset<? extends Instance> data = generateData();
        fc.hierarchy(data, new Props());
    }

    @Test
    public void testGetName() {
        FastCommunity fc = new FastCommunity();
        assertEquals("Fast Community", fc.getName());
    }
}