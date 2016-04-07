/**
 * %HEADER%
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
