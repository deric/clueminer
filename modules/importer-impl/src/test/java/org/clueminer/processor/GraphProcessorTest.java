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
package org.clueminer.processor;

import java.io.IOException;
import org.clueminer.fixtures.NetworkFixture;
import org.clueminer.importer.impl.GraphDraft;
import org.clueminer.io.importer.api.Container;
import org.junit.Test;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author deric
 */
public class GraphProcessorTest {

    private static final NetworkFixture NF = new NetworkFixture();
    private GraphProcessor subject;

    @Test
    public void testKarate() throws IOException {
        subject = new GraphProcessor();
        Container container = new GraphDraft();
        container.setFile(FileUtil.toFileObject(NF.karate()));
        subject.setContainer(container);

        // TODO; ensure data was loaded into a graph
        //subject.run();


    }

}
