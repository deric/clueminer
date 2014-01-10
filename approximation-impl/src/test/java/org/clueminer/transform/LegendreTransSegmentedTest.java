package org.clueminer.transform;

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.dataset.plugin.AttrHashDataset;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.dataset.row.TimeRow;
import org.clueminer.fixtures.TimeseriesFixture;
import org.clueminer.io.CsvLoader;
import org.clueminer.types.TimePoint;
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

    private static final TimeseriesFixture fixtures = new TimeseriesFixture();
    private static Timeseries<ContinuousInstance> simple;
    private static LegendreTransSegmented subject;

    public LegendreTransSegmentedTest() {
        subject = new LegendreTransSegmented();
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        File f = fixtures.data01();

        CSVReader csv = new CSVReader(new FileReader(f));
        TimeRow inst = new TimeRow(Double.class, 15);
        int i = 0;
        String[] row;
        double value;
        while ((row = csv.readNext()) != null) {
            value = Double.valueOf(row[0]);
            inst.put(value);
            i++;
        }
        csv.close();

        simple = generateDataset(1, i);
        simple.add(inst);
    }

    private static TimeseriesDataset generateDataset(int capacity, int attrCnt) {
        TimePointAttribute[] tp = new TimePointAttribute[attrCnt];
        for (int j = 0; j < tp.length; j++) {
            tp[j] = new TimePointAttribute(j, j, j);
        }

        return new TimeseriesDataset<ContinuousInstance>(capacity, tp);
    }

    private static ContinuousInstance generateInstance(int attrCnt) {
        TimeRow inst = new TimeRow(Double.class, attrCnt);
        Random rand = new Random();
        for (int i = 0; i < attrCnt; i++) {
            inst.set(i, rand.nextDouble());
        }
        return inst;
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
        int segments = 3;
        int degree = 7;
        // 7 is the default degree of Legendre
        Dataset<Instance> output = new ArrayDataset<Instance>(10, segments * degree);
        //analyze data
        ph.start(segments * simple.size());
        subject.analyze(simple, output, ph, segments, degree);
        assertEquals(1, output.size());
        for (int i = 0; i < output.attributeCount(); i++) {
            //check that all attributes were assigned some value
            System.out.println("attr [" + i + "] = " + output.getAttributeValue(i, 0));
            assertEquals(true, output.getAttributeValue(i, 0) != 0.0);
        }
        ph.finish();
    }

    @Test
    public void testAnalyzeTimeseries2() {
        ProgressHandle ph = ProgressHandleFactory.createHandle("Trasforming dataset");
        int segments = 3;
        int degree = 7;
        // 7 is the default degree of Legendre
        Dataset<Instance> output = new AttrHashDataset<Instance>(10);
        //analyze data
        ph.start(segments * simple.size());
        subject.analyze(simple, output, ph, segments, degree);
        assertEquals(1, output.size());
        for (int i = 0; i < output.attributeCount(); i++) {
            //check that all attributes were assigned some value
            System.out.println("attr [" + i + "] = " + output.getAttributeValue(i, 0));
            assertEquals(true, output.getAttributeValue(i, 0) != 0.0);
        }
        ph.finish();
    }

    /**
     * Test of createDefaultOutput method, of class LegendreTransSegmented.
     */
    @Test
    public void testCreateDefaultOutput() {
    }

    /**
     * Test of splitIntoSegments method, of class LegendreTransSegmented.
     */
    @Test
    public void testSplitIntoSegments() {
        int size = 5;
        int attrCnt = 10;
        Timeseries<ContinuousInstance> t1 = generateDataset(size, attrCnt);
        for (int i = 0; i < size; i++) {
            t1.add(generateInstance(attrCnt));
        }

        Timeseries<ContinuousInstance>[] res = subject.splitIntoSegments(t1, 3);
        //split dataset into 3 segments
        assertEquals(3, res[0].attributeCount());
        assertEquals(3, res[1].attributeCount());
        //last one should contain remaining values
        assertEquals(4, res[2].attributeCount());

    }

    @Test
    public void testRealWorldTs() {
        String separator = ",";
        TimeseriesFixture tf = new TimeseriesFixture();
        File file = tf.ts01();
        Timeseries<ContinuousInstance> dataset = new TimeseriesDataset<ContinuousInstance>(254);
        CsvLoader loader = new CsvLoader();
        ArrayList<Integer> metaAttr = new ArrayList<Integer>();
        //ArrayList<Integer> skipAttr = new ArrayList<Integer>();
        //skipped.add(0); //first one is ID
        for (int i = 1; i < 7; i++) {
            metaAttr.add(i);
            //  skipAttr.add(i);
        }
        for (int j = 0; j < 7; j++) {
            loader.addNameAttr(j); //meta attributes
        }
        loader.setNameJoinChar(", ");

        String[] firstLine = CsvLoader.firstLine(file, separator);
        int i = 0;
        int index;
        int last = firstLine.length;
        int offset = 6;
        TimePoint tp[] = new TimePointAttribute[last - offset];
        double pos;
        for (String item : firstLine) {
            if (i > offset) {
                index = i - offset - 1;
                pos = Double.valueOf(item);
                tp[index] = new TimePointAttribute(index, index, pos);
            }
            i++;
        }
        dataset.setTimePoints(tp);
        loader.setMetaAttr(metaAttr);
        //loader.setSkipIndex(skipAttr);
        loader.setSeparator(separator);
        loader.setClassIndex(0);
        loader.setSkipHeader(true);
        Dataset<? extends Instance> d = (Dataset<? extends Instance>) dataset;
        loader.setDataset(d);
        loader.load(file);

        Timeseries<ContinuousInstance>[] res = subject.splitIntoSegments(dataset, 3);
    }
}
