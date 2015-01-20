package org.clueminer.chinesewhispers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class ChineseWhispersTest {

	@Test
	public void testGetName() {
		ChineseWhispers cw = new ChineseWhispers();
		assertEquals("Chinese Whispers", cw.getName());
	}
}
