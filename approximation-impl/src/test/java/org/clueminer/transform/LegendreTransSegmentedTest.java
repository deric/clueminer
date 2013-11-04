package org.clueminer.transform;

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.AttrHashDataset;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.dataset.row.TimeRow;
import org.clueminer.fixtures.TimeseriesFixture;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 *
 * @author deric
 */
public class LegendreTransSegmentedTest {

    private static TimeseriesFixture fixtures = new TimeseriesFixture();
    private static Timeseries<ContinuousInstance> simple;
    private static LegendreTransSegmented subject;

    public LegendreTransSegmentedTest() {
        subject = new LegendreTransSegmented();
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        File f = fixtures.data01();


        CSVReader csv = new CSVReader(new FileReader(f));
        ContinuousInstance inst = new TimeRow(15);
        int i = 0;
        String[] row;
        double value;
        while ((row = csv.readNext()) != null) {
            value = Double.valueOf(row[0]);
            inst.put(value);
            i++;
        }
        csv.close();

        TimePointAttribute[] tp = new TimePointAttribute[i];
        for (int j = 0; j < tp.length; j++) {
            tp[j] = new TimePointAttribute(j, j, j);
        }

        simple = new TimeseriesDataset<ContinuousInstance>(1, tp);
        simple.add(inst);

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void dataLoaded() {
        assertEquals(1, simple.size());
        assertEquals(15, simple.attributeCount());
    }

    /**
     * Test of getName method, of class LegendreTransSegmented.
     */
    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    /**
     * Test of analyze method, of class LegendreTransSegmented.
     */
    @Test
    public void testAnalyze() {
    }

    /**
     * Test of analyzeTimeseries method, of class LegendreTransSegmented.
     */
    @Test
    public void testAnalyzeTimeseries() {
        ProgressHandle ph = ProgressHandleFactory.createHandle("Trasforming dataset");
        Dataset<Instance> output = new AttrHashDataset<Instance>(10);
        //analyze data
        subject.analyze(simple, output, ph);

        //assertEquals(5, output.size());
    }

    /**
     * Test of createDefaultOutput method, of class LegendreTransSegmented.
     */
    @Test
    public void testCreateDefaultOutput() {
    }
}