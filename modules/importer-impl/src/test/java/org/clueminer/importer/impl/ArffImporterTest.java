package org.clueminer.importer.impl;

import java.io.File;
import java.io.IOException;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.ContainerLoader;
import org.clueminer.types.FileType;
import org.junit.After;
import static org.junit.Assert.*;
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

    public ArffImporterTest() {
    }

    @Before
    public void setUp() {
        subject = new ArffImporter();
    }

    @After
    public void tearDown() {
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
        Container container = new ImportContainerImpl();
        subject.execute(container, insect);
        ContainerLoader loader = container.getLoader();
        //name of relation from ARFF
        assertEquals("insect", loader.getName());
        //two attributes and class
        assertEquals(4, loader.getAttributeCount());
        assertEquals(30, loader.getInstanceCount());
    }

    @Test
    public void testIris() throws IOException {
        File iris = fixtures.irisArff();
        Container container = new ImportContainerImpl();
        subject.execute(container, iris);
        ContainerLoader loader = container.getLoader();
        //name of relation from ARFF
        assertEquals("iris", loader.getName());
        //two attributes and class
        assertEquals(5, loader.getAttributeCount());
        assertEquals(150, loader.getInstanceCount());
    }

    @Test
    public void testVehicle() throws IOException {
        File vehicle = fixtures.vehicleArff();
        Container container = new ImportContainerImpl();
        subject.execute(container, vehicle);
        ContainerLoader loader = container.getLoader();
        //name of relation from ARFF
        assertEquals("vehicle", loader.getName());
        //two attributes and class
        assertEquals(19, loader.getAttributeCount());
        assertEquals(846, loader.getInstanceCount());
        //there are 4 classes in the dataset
        //assertNotNull(loader.getDataset());
        //assertEquals(4, loader.getDataset().getClasses().size());
    }

    @Test
    public void testExecute_Container_FileObject() throws Exception {
    }

    @Test
    public void testCancel() {
    }

}
