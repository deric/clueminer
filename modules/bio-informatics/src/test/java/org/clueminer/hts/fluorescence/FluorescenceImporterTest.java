/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.hts.fluorescence;

import java.io.IOException;
import org.clueminer.fixtures.BioFixture;
import org.clueminer.hts.api.HtsInstance;
import org.clueminer.hts.api.HtsPlate;
import static org.junit.Assert.*;
import org.junit.Test;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class FluorescenceImporterTest {

    private static FluorescenceImporter importer;
    private static double delta = 1e-9;

    public FluorescenceImporterTest() {
    }

    /**
     * Test of run method, of class FluorescenceImporter.
     */
    @Test
    public void testRun() {

        try {
            BioFixture tf = new BioFixture();
            importer = new FluorescenceImporter(tf.apData());
            ProgressHandle ph = ProgressHandle.createHandle("Importing dataset");
            importer.setProgressHandle(ph);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        importer.run();
        HtsPlate<HtsInstance> dataset = importer.getDataset();
        //should work, but we use tmp files extracted from jar, which have different name
        //assertEquals("AP-01_2012112", dataset.getName());
        assertEquals(15, dataset.attributeCount());
        assertEquals(1536, dataset.size());

        HtsInstance inst = dataset.instance(0);
        assertEquals("A1", inst.getName());
        assertEquals(424, inst.value(0), delta);
        assertEquals(4087, inst.value(dataset.attributeCount() - 1), delta);
        assertEquals(15, inst.size());
        System.out.println("size: " + inst.size());
        System.out.println("a1: " + inst.toString());
        assertEquals("CP-001073", dataset.getName());
    }

}
