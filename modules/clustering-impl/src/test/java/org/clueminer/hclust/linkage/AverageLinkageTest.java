package org.clueminer.hclust.linkage;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AverageLinkageTest {

    private final AverageLinkage subject = new AverageLinkage();
    private final double delta = 1e-9;

    @Test
    public void testDistance() {
    }

    @Test
    public void testSimilarity() {
    }

    @Test
    public void testAlphaA() {
        assertEquals(0.25, subject.alphaA(1, 3, 1), delta);
    }

    @Test
    public void testAlphaB() {
        assertEquals(0.75, subject.alphaB(1, 3, 1), delta);
    }

    @Test
    public void testBeta() {
        assertEquals(0.0, subject.beta(1, 2, 3), delta);
    }

    @Test
    public void testGamma() {
        assertEquals(0.0, subject.gamma(), delta);
    }

}
