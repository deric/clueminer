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
package org.clueminer.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.CommonFixture;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ARFFWriterTest {

    private ARFFWriter subject;
    private static CommonFixture tf;

    @BeforeClass
    public static void setUpClass() {
        tf = new CommonFixture();
    }

    @Test
    public void testWriteHeader() throws IOException, FileNotFoundException, ParserError {
        File tmpFile;
        FileWriter fwriter;
        ARFFWriter writer;
        Dataset<Instance> dataset = (Dataset<Instance>) NumericalStatsTest.irisDataset();
        try {
            tmpFile = File.createTempFile("arffWriterTest", ".arff");
            tmpFile.deleteOnExit();
            fwriter = new FileWriter(tmpFile);
            writer = new ARFFWriter(fwriter);
            writer.writeHeader(dataset, new String[]{"1", "2"});
            writer.flush();
            String data = readFile(tmpFile.getPath(), StandardCharsets.UTF_8);
            assertTrue(data.startsWith("@RELATION"));
            System.out.println("ARFF: " + data);
        } catch (IOException e) {
            fail();
        }
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    @Test
    public void testWriteLine_String() {
    }

    @Test
    public void testWriteLine_StringBuilder() {
    }

}
