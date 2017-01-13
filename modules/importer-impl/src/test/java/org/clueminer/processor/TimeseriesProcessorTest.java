/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.processor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.TimeseriesFixture;
import org.clueminer.importer.impl.CsvImporter;
import org.clueminer.importer.impl.DraftContainer;
import org.clueminer.importer.impl.ImportUtils;
import org.clueminer.io.importer.api.Container;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 * Test timeseries data import
 *
 * @author deric
 */
public class TimeseriesProcessorTest {

    private final CsvImporter csv = new CsvImporter();
    private TimeseriesProcessor subject;
    private Container container;
    private static final TimeseriesFixture TF = new TimeseriesFixture();

    public TimeseriesProcessorTest() {
        subject = new TimeseriesProcessor();
    }

    @Before
    public void setUp() {
        subject = new TimeseriesProcessor();
        container = new DraftContainer();
    }

    /**
     * Read from file
     *
     * @throws IOException
     */
    @Test
    public void testFileRead() throws IOException {
        File tsFile = TF.ap01();
        csv.execute(container, tsFile);
        assertNotNull(container.getReport());
        assertEquals(16, container.getAttributeCount());
        assertEquals(1536, container.getInstanceCount());
    }

    /**
     * Fetch data from a reader
     *
     * @throws IOException
     */
    @Test
    public void testTimeSeries() throws IOException {
        File tsFile = TF.ap01();
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(tsFile.getAbsolutePath()));
        Reader reader = ImportUtils.getTextReader(stream);
        //run import
        csv.execute(container, reader);

        subject.setContainer(container);
        //convert preloaded data to a real dataset
        subject.run();

        //name of relation from ARFF
        Dataset<? extends Instance> dataset = container.getDataset();
        //assertEquals("AP01", dataset.getName());
        /**
         * TODO: fix creating attributes
         */
        assertEquals(15, dataset.attributeCount());
        assertEquals(1536, dataset.size());
        assertNotNull(container.getDataset());
        //assertEquals(4, container.getDataset().getClasses().size());
    }

}
