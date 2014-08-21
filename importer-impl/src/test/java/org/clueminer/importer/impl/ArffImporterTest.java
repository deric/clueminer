package org.clueminer.importer.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        File banana = fixtures.bananaArff();
        Container container = new ImportContainerImpl();
        BufferedReader reader = new BufferedReader(new FileReader(banana));
        subject.execute(container, reader);
        ContainerLoader loader = container.getLoader();
        //name of relation from ARFF
        assertEquals("banana1", loader.getName());
        //two attributes and class
        assertEquals(3, loader.getAttributeCount());
        assertEquals(4811, loader.getInstanceCount());
    }

    @Test
    public void testExecute_Container_FileObject() throws Exception {
    }

    @Test
    public void testCancel() {
    }

}
