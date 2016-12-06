/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.transform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.dataset.impl.AttrHashDataset;
import org.clueminer.dataset.impl.TimeseriesDataset;
import org.clueminer.dataset.row.TimeRow;
import org.clueminer.fixtures.TimeseriesFixture;
import org.clueminer.io.csv.CsvLoader;
import org.clueminer.types.TimePoint;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author deric
 */
public class LegendreTransSegmentedTest extends TsTest {

    private static LegendreTransSegmented subject;
    private Timeseries<ContinuousInstance> simple;

    public LegendreTransSegmentedTest() throws IOException {
        subject = new LegendreTransSegmented();
        simple = loadData01();
    }

    private static ContinuousInstance generateInstance(int attrCnt) {
        TimeRow inst = new TimeRow(Double.class, attrCnt);
        Random rand = new Random();
        for (int i = 0; i < attrCnt; i++) {
            inst.set(i, rand.nextDouble());
        }
        return inst;
    }

    @Before
    public void setUp() {
    }

    @Test
    public void dataLoaded() {
        assertEquals(1, simple.size());
        assertEquals(15, simple.attributeCount());
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testAnalyzeTimeseries() {
        ProgressHandle ph = ProgressHandle.createHandle("Trasforming dataset");
        int segments = 3;
        int degree = 7;
        // 7 is the default degree of Legendre
        Dataset<Instance> output = new ArrayDataset<>(10, segments * degree);
        //analyze data
        ph.start(segments * simple.size());
        subject.analyze(simple, output, ph, segments, degree);
        assertEquals(1, output.size());
        for (int i = 0; i < output.attributeCount(); i++) {
            //check that all attributes were assigned some value
            System.out.println("attr [" + i + "] = " + output.get(0, i));
            assertEquals(true, output.get(0, i) != 0.0);
        }
        ph.finish();
    }

    @Test
    public void testAnalyzeTimeseries2() {
        ProgressHandle ph = ProgressHandle.createHandle("Trasforming dataset");
        int segments = 3;
        int degree = 7;
        // 7 is the default degree of Legendre
        Dataset<Instance> output = new AttrHashDataset<>(10);
        //analyze data
        ph.start(segments * simple.size());
        subject.analyze(simple, output, ph, segments, degree);
        assertEquals(1, output.size());
        for (int i = 0; i < output.attributeCount(); i++) {
            //check that all attributes were assigned some value
            System.out.println("attr [" + i + "] = " + output.get(0, i));
            assertEquals(true, output.get(0, i) != 0.0);
        }
        ph.finish();
    }

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
    public void testRealWorldTs() throws IOException {
        char separator = ',';
        TimeseriesFixture tf = new TimeseriesFixture();
        File file = tf.ts01();
        Timeseries<ContinuousInstance> dataset = new TimeseriesDataset<>(254);
        CsvLoader loader = new CsvLoader();
        ArrayList<Integer> metaAttr = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            metaAttr.add(i);
        }
        for (int j = 0; j < 7; j++) {
            loader.addNameAttr(j); //meta attributes
        }

        String[] firstLine = CsvLoader.firstLine(file, String.valueOf(separator));
        int i = 0;
        int index;
        int last = firstLine.length;
        int offset = metaAttr.size();
        TimePoint tp[] = new TimePointAttribute[last - offset];
        double pos;
        for (String item : firstLine) {
            if (i >= offset) {
                index = i - offset;
                pos = Double.valueOf(item);
                tp[index] = new TimePointAttribute(index, index, pos);
            }
            i++;
        }
        System.out.println("tp length " + tp.length);
        dataset.setTimePoints(tp);
        loader.setMetaAttr(metaAttr);
        loader.setSeparator(separator);
        loader.setSkipHeader(true);
        Dataset<? extends Instance> d = (Dataset<? extends Instance>) dataset;
        loader.setDataset(d);
        loader.load(file);

        Timeseries<ContinuousInstance>[] res = subject.splitIntoSegments(dataset, 3);

        assertEquals(4, res[0].attributeCount());
        assertEquals(4, res[1].attributeCount());
        assertEquals(6, res[2].attributeCount());
    }
}
