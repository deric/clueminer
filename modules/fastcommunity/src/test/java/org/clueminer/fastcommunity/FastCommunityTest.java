package org.clueminer.fastcommunity;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class FastCommunityTest {

	private Dataset<? extends Instance> generateData() {
		double[][] data = new double[][]{
			/* n0 n1 n2 n3 n4 n5 n6 */
			/* n0 */{0, 1, 1, 0, 0, 0, 0},
			/* n1 */ {1, 0, 0, 0, 0, 0, 0},
			/* n2 */ {1, 0, 0, 0, 0, 0, 0},
			/* n3 */ {0, 0, 0, 0, 0, 0, 0},
			/* n4 */ {0, 0, 0, 0, 0, 1, 1},
			/* n5 */ {0, 0, 0, 0, 1, 0, 0},
			/* n6 */ {0, 0, 0, 0, 1, 0, 0}
		};
		Dataset<Instance> dataset = new ArrayDataset<>(data);
		return dataset;
	}

	@Test
	public void clusterTest() {
		System.out.println("Hierarchy Test");
		FastCommunity fc = new FastCommunity();
		Dataset<? extends Instance> data = generateData();
		fc.hierarchy(data, null);
	}

	@Test
	public void testGetName() {
		FastCommunity fc = new FastCommunity();
		assertEquals("Fast Community", fc.getName());
	}
}
