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
package org.clueminer.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LineIteratorFactory {

    public static LineIterator createFromGZip(File f) throws IOException {
        return new LineIterator(new GZIPInputStream(new FileInputStream(f)));
    }

    public static LineIterator createFromZip(File f) throws IOException {
        ZipInputStream zipinputstream = new ZipInputStream(new FileInputStream(f));

        ZipEntry zipentry = zipinputstream.getNextEntry();
        if (zipentry != null) {
            // for each entry to be extracted
            String entryName = zipentry.getName();
            // RandomAccessFile rf;
            File newFile = new File(entryName);
            return new LineIterator(newFile);

            // out = read(it, classIndex, separator);
            // zipinputstream.closeEntry();
        }
        return null;
    }
}
