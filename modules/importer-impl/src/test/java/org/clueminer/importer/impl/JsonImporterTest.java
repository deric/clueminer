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
package org.clueminer.importer.impl;

import java.io.FileReader;
import java.util.List;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.importer.Issue;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.io.importer.api.Report;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class JsonImporterTest {

    private final JsonImporter subject = new JsonImporter();
    private static final CommonFixture CF = new CommonFixture();

    @Test
    public void testImportData() throws Exception {
        DraftContainer<InstanceDraft> loader = new DraftContainer();
        FileReader reader = new FileReader(CF.simpleJson());

        subject.execute(loader, reader);
        for (AttributeDraft attr : loader.getAttrIter()) {
            System.out.println("attr: " + attr);
        }
        assertEquals(14, loader.attributeCount());
        assertEquals(6, loader.size());
        assertNotNull(loader.get(0));
        assertEquals(14, loader.get(0).size());
        assertNotNull(loader.getReport());
        Report report = loader.getReport();
        List<Issue> issues = report.getIssues();
        for (int i = 0; i < 5; i++) {
            if (i < issues.size()) {
                System.out.println(report.getIssues().get(i));
            }
        }
        System.out.println("JSON import: " + report.getIssues().size() + " issues");

    }

}
