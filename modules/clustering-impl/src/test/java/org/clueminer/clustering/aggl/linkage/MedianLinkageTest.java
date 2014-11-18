package org.clueminer.clustering.aggl.linkage;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MedianLinkageTest extends AbstractLinkageTest {

    public MedianLinkageTest() {
        subject = new MedianLinkage();
    }


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
