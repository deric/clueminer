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
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.fixtures.MLearnFixture;
import org.clueminer.importer.impl.ArffImporter;
import org.clueminer.importer.impl.CsvImporter;
import org.clueminer.importer.impl.DraftContainer;
import org.clueminer.importer.impl.ImportUtils;
import org.clueminer.importer.impl.JsonImporter;
import org.clueminer.io.importer.api.Container;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Test data loading process
 *
 * @author deric
 */
public class DefaultProcessorTest {

    private final ArffImporter arff = new ArffImporter();
    private static final CommonFixture CF = new CommonFixture();
    private static final MLearnFixture MF = new MLearnFixture();

    @Test
    public void testIrisFromFile() throws IOException {
        File iris = CF.irisArff();
        Container container = new DraftContainer();
        arff.execute(container, iris);
        //just parse ARFF into container
        assertEquals(5, container.getAttributeCount());
        assertEquals(150, container.getInstanceCount());
    }

    @Test
    public void testIris() throws IOException {
        File iris = CF.irisArff();
        Container container = new DraftContainer();
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(iris.getAbsolutePath()));
        Reader reader = ImportUtils.getTextReader(stream);
        //run import
        arff.execute(container, reader);
        //load data into container
        // 4 numeric attributes + class
        assertEquals(5, container.getAttributeCount());
        assertEquals(150, container.getInstanceCount());

        DefaultProcessor subject = new DefaultProcessor();
        subject.setContainer(container);
        //convert preloaded data to a real dataset
        subject.run();

        assertEquals("iris", container.getName());
        Dataset<? extends Instance> dataset = container.getDataset();
        assertNotNull(dataset);
        assertEquals(4, dataset.attributeCount());
        assertEquals(150, dataset.size());
        //assertEquals(3, dataset.getClasses().size());
    }

    @Test
    public void testVehicleFromFile() throws IOException {
        File vehicle = CF.vehicleArff();
        Container container = new DraftContainer();
        arff.execute(container, vehicle);
        //just parse ARFF into container
        assertEquals(19, container.getAttributeCount());
        assertEquals(846, container.getInstanceCount());
    }

    @Test
    public void testVehicle() throws IOException {
        File vehicle = CF.vehicleArff();
        Container container = new DraftContainer();
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(vehicle.getAbsolutePath()));
        Reader reader = ImportUtils.getTextReader(stream);
        //run import
        arff.execute(container, reader);
        assertEquals(19, container.getAttributeCount());
        assertEquals(846, container.getInstanceCount());
        DefaultProcessor subject = new DefaultProcessor();
        subject.setContainer(container);
        //convert preloaded data to a real dataset
        subject.run();

        //importer.execute(container, stream);
        //importer.run();
        //name of relation from ARFF
        assertEquals("vehicle", container.getName());
        Dataset<? extends Instance> dataset = container.getDataset();
        assertEquals(18, dataset.attributeCount());
        assertEquals(846, dataset.size());
        //there are 4 classes in the dataset
        assertNotNull(container.getDataset());
        //assertEquals(4, loader.getDataset().getClasses().size());
    }

    @Test
    public void testMdData() throws IOException {
        File json = CF.simpleJson();
        Container container = new DraftContainer();
        JsonImporter importer = new JsonImporter();
        importer.execute(container, json);

        assertEquals(14, container.getAttributeCount());
        assertEquals(6, container.getInstanceCount());

        DefaultProcessor subject = new DefaultProcessor();
        subject.setContainer(container);
        //convert preloaded data to a real dataset
        subject.run();

        Dataset<? extends Instance> dataset = container.getDataset();
        assertEquals(1, dataset.attributeCount());
        assertEquals(6, dataset.size());
        for (int i = 0; i < dataset.size(); i++) {
            System.out.println(i + ": " + dataset.get(i).size());
        }
    }

    @Test
    public void testCsvData() throws IOException {
        File csv = MF.correlations();
        Container container = new DraftContainer();
        CsvImporter importer = new CsvImporter();
        importer.execute(container, csv);

        assertEquals(40, container.getAttributeCount());
        assertEquals(27, container.getInstanceCount());

        DefaultProcessor subject = new DefaultProcessor();
        subject.setContainer(container);
        //convert preloaded data to a real dataset
        subject.run();

        Dataset<? extends Instance> dataset = container.getDataset();
        assertEquals(39, dataset.attributeCount());
        assertEquals(27, dataset.size());
        for (int i = 0; i < dataset.size(); i++) {
            assertEquals(39, dataset.get(i).size());
            System.out.println(i + ": " + dataset.get(i).size());
        }
    }

}
