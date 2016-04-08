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
import org.clueminer.fixtures.CommonFixture;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class JsonImporterTest {

    private JsonImporter subject = new JsonImporter();
    private static CommonFixture CF = new CommonFixture();

    @Test
    public void testImportData() throws Exception {
        DraftContainer loader = new DraftContainer();
        FileReader reader = new FileReader(CF.simpleJson());

        subject.importData(loader, reader);
        for (Object attr : loader.getAttrIter()) {
            System.out.println("attr: " + attr);
        }
        assertEquals(13, loader.attributeCount());
        //    assertEquals(5, loader.size());
    }

}
