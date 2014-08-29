package org.clueminer.importer.impl;

import java.io.IOException;
import java.util.Collection;
import org.clueminer.fixtures.ImageFixture;
import org.clueminer.fixtures.MLearnFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class MimeHelperTest {

    private final MLearnFixture fixtures = new MLearnFixture();

    private final MimeHelper subject = new MimeHelper();

    public MimeHelperTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDetectMIME() throws IOException {
        Collection col = subject.detectMIME(fixtures.iris());
        System.out.println("iris: " + col.toString());
        assertEquals(true, col.contains("application/octet-stream"));
        col = subject.detectMIME(fixtures.dermatology());
        System.out.println(col);
        //this might be platform dependent
        //assertEquals(true, col.contains("text/x-tex"));
        ImageFixture inf = new ImageFixture();
        col = subject.detectMIME(inf.insect3d());
        System.out.println("image: " + col);

    }


}
