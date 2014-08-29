package org.clueminer.clustering.preview;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.TimeseriesFixture;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MetaLoaderRunnerTest {

    private MetaLoaderRunner subject = new MetaLoaderRunner();
    private static final TimeseriesFixture fixture = new TimeseriesFixture();

    public MetaLoaderRunnerTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of run method, of class MetaLoaderRunner.
     */
    @Test
    public void testRun() {
    }

    @Test
    public void testLoadMeta() throws IOException {
        File file = fixture.ts01();
        Dataset<? extends Instance> dataset = subject.loadMTimeseries(file);
        assertEquals(100, dataset.size());
        assertEquals(14, dataset.attributeCount());
    }

    /**
     * Test of assignColours method, of class MetaLoaderRunner.
     */
    @Test
    public void testAssignColours() throws IOException {
        File file = fixture.ts01();
        Dataset<? extends Instance> dataset = subject.loadMTimeseries(file);
        Map<Integer, Color> colors = subject.assignColours(dataset);
        //number of distinct primary keys
        assertEquals(5, colors.size());
    }

    /**
     * Test of getResult method, of class MetaLoaderRunner.
     */
    @Test
    public void testGetResult() {
    }

}
