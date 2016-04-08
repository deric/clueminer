package org.clueminer.processor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.importer.impl.ArffImporter;
import org.clueminer.importer.impl.DraftContainer;
import org.clueminer.importer.impl.ImportUtils;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.ContainerLoader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DefaultProcessorTest {

    private final ArffImporter arff = new ArffImporter();
    private static final CommonFixture fixtures = new CommonFixture();

    public DefaultProcessorTest() {
    }

    @Test
    public void testIris() throws IOException {
        File iris = fixtures.irisArff();
        Container container = new DraftContainer();
        arff.execute(container, iris);
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(iris.getAbsolutePath()));
        Reader reader = ImportUtils.getTextReader(stream);
        //run import
        arff.execute(container, reader);
        ContainerLoader loader = container.getLoader();
        DefaultProcessor subject = new DefaultProcessor();

        subject.setContainer(loader);
        //convert preloaded data to a real dataset
        subject.run();

        assertEquals("iris", loader.getName());
        Dataset<? extends Instance> dataset = loader.getDataset();
        assertNotNull(dataset);
        assertEquals(4, dataset.attributeCount());
        assertEquals(150, dataset.size());
        //assertEquals(3, dataset.getClasses().size());
    }

    @Test
    public void testVehicle() throws IOException {
        File vehicle = fixtures.vehicleArff();
        Container container = new DraftContainer();
        arff.execute(container, vehicle);
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(vehicle.getAbsolutePath()));
        Reader reader = ImportUtils.getTextReader(stream);
        //run import
        arff.execute(container, reader);
        ContainerLoader loader = container.getLoader();
        DefaultProcessor subject = new DefaultProcessor();

        subject.setContainer(loader);
        //convert preloaded data to a real dataset
        subject.run();

        //importer.execute(container, stream);
        //importer.run();
        //name of relation from ARFF
        assertEquals("vehicle", loader.getName());
        Dataset<? extends Instance> dataset = loader.getDataset();
        assertEquals(18, dataset.attributeCount());
        assertEquals(846, dataset.size());
        //there are 4 classes in the dataset
        assertNotNull(loader.getDataset());
        //assertEquals(4, loader.getDataset().getClasses().size());
    }

}
