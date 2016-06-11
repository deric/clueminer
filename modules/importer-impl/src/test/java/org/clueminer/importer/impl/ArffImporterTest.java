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
package org.clueminer.importer.impl;

import java.io.File;
import java.io.IOException;
import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.types.FileType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author deric
 */
public class ArffImporterTest {

    private ArffImporter subject;
    private static final CommonFixture fixtures = new CommonFixture();

    @Before
    public void setUp() {
        subject = new ArffImporter();
    }

    @Test
    public void testGetName() {
        assertEquals("ARFF", subject.getName());
    }

    @Test
    public void testIsAccepting() throws IOException {
        MimeHelper helper = new MimeHelper();
        File banana = fixtures.bananaArff();
        assertEquals(true, subject.isAccepting(helper.detectMIME(banana)));

        File dermatology = fixtures.dermatologyArff();
        assertEquals(true, subject.isAccepting(helper.detectMIME(dermatology)));
    }

    @Test
    public void testGetFileTypes() throws IOException {
        FileType[] ft = subject.getFileTypes();
        assertNotNull(ft[0]);
    }

    @Test
    public void testIsMatchingImporter() throws IOException {
        File banana = fixtures.bananaArff();
        FileObject fo = FileUtil.toFileObject(banana);
        if (fo != null) {
            assertEquals(true, subject.isMatchingImporter(fo));
        }
    }

    @Test
    public void testExecute_Container_Reader() throws Exception {
        File insect = fixtures.insectArff();
        Container container = new DraftContainer();
        subject.execute(container, insect);
        //name of relation from ARFF
        assertEquals("insect", container.getName());
        //two attributes and class
        assertEquals(4, container.getAttributeCount());
        assertEquals(30, container.getInstanceCount());
    }

    @Test
    public void testIris() throws IOException {
        File iris = fixtures.irisArff();
        Container container = new DraftContainer();
        subject.execute(container, iris);
        //name of relation from ARFF
        assertEquals("iris", container.getName());
        //two attributes and class
        assertEquals(5, container.getAttributeCount());
        assertEquals(150, container.getInstanceCount());
    }

    @Test
    public void testVehicle() throws IOException {
        File vehicle = fixtures.vehicleArff();
        Container container = new DraftContainer();
        subject.execute(container, vehicle);
        //name of relation from ARFF
        assertEquals("vehicle", container.getName());
        //two attributes and class
        assertEquals(19, container.getAttributeCount());
        assertEquals(846, container.getInstanceCount());
        //there are 4 classes in the dataset
        //assertNotNull(loader.getDataset());
        //assertEquals(4, loader.getDataset().getClasses().size());
    }

    @Test
    public void testClassDefinition() {
        assertEquals(true, subject.isClassDefinition("@attribute class {1,2,3,4,5,6,7,8,9,10}"));
        assertEquals(true, subject.isClassDefinition("@attribute class {not_recom,recommend,very_recom,priority,spec_prior}"));
        assertEquals(true, subject.isClassDefinition("@attribute 'Class' {opel,saab,bus,van}"));
        assertEquals(true, subject.isClassDefinition("@ATTRIBUTE	class	{b,g}"));
    }

    @Test
    public void testIonosphere() throws IOException {
        File iono = fixtures.ionosphereArff2();
        Container<InstanceDraft> container = new DraftContainer();
        subject.execute(container, iono);
        //name of relation from ARFF
        assertEquals("ionosphere", container.getName());
        //two attributes and class
        assertEquals(35, container.getAttributeCount());
        assertEquals(351, container.getInstanceCount());

        AttributeDraft attrd;
        for (int i = 0; i < container.getAttributeCount(); i++) {
            attrd = container.getAttribute(i);
            if (i < 34) {
                System.out.println("attr " + i + ": " + attrd.getRole().toString());
                assertEquals(BasicAttrRole.INPUT, attrd.getRole());
            } else {
                assertEquals(BasicAttrRole.CLASS, attrd.getRole());
            }
        }
    }

}
