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

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import org.clueminer.fixtures.NetworkFixture;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class GmlImporterTest {

    private GmlImporter subject = new GmlImporter();
    private static NetworkFixture nf = new NetworkFixture();


    @Before
    public void setUp() {
    }

    @Test
    public void testParsingTypes() throws IOException {

        GraphDraft container = new GraphDraft();

        Reader reader = new FileReader(nf.karate());
        try {
            subject.execute(container, reader);

            assertEquals(34, container.getNumNodes());
            assertEquals(78, container.getNumEdges());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }


}
