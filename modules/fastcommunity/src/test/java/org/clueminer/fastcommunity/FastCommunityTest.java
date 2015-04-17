package org.clueminer.fastcommunity;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class FastCommunityTest {

	@Test
	public void testGetName() {
		FastCommunity fc = new FastCommunity();
		assertEquals("Fast Community", fc.getName());
	}
}
