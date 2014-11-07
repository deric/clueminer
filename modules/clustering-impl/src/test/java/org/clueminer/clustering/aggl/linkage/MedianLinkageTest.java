package org.clueminer.clustering.aggl.linkage;

import org.clueminer.clustering.aggl.linkage.MedianLinkage;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MedianLinkageTest {

    private final MedianLinkage subject = new MedianLinkage();
    private final double delta = 1e-9;

    @Test
    public void testDistance() {
    }

    @Test
    public void testSimilarity() {
    }

    @Test
    public void testAlphaA() {
        assertEquals(4, subject.alphaA(1, 3, 99), delta);
    }

    @Test
    public void testAlphaB() {
        assertEquals(0.75, subject.alphaB(1, 3, 1), delta);
    }

    @Test
    public void testBeta() {
        assertEquals(-0.25, subject.beta(2, 2, 99), delta);
    }

    @Test
    public void testGamma() {
        assertEquals(0.0, subject.gamma(), delta);
    }

}
