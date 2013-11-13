package org.clueminer.magic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.clueminer.fixtures.CommonFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tombart
 */
public class TxtDetectTest {

    private TxtDetect subject;
    private CommonFixture fixture = new CommonFixture();

    public TxtDetectTest() {
        subject = new TxtDetect();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of detect method, of class TxtDetect.
     */
    @Test
    public void testDetect() throws FileNotFoundException, IOException {
        File file = fixture.irisData();
        BufferedReader br = new BufferedReader(new FileReader(file));

        subject.detect(br);
    }
}
