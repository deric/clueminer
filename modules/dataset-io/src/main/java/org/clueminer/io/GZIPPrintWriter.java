/**
 * %HEADER%
 */
package org.clueminer.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;


public class GZIPPrintWriter extends PrintWriter {

    public GZIPPrintWriter(File file, String csn) throws IOException {
        super(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(ExtensionManager.extension(file, "gz"))),csn));
    }

    public GZIPPrintWriter(File file) throws IOException {
        super(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(ExtensionManager.extension(file, "gz")))));
    }

    public GZIPPrintWriter(OutputStream out, boolean autoFlush) throws IOException {
        super(new OutputStreamWriter(new GZIPOutputStream(out)));
    }

    public GZIPPrintWriter(OutputStream out) throws IOException {
        super(new OutputStreamWriter(new GZIPOutputStream(out)));
    }

    public GZIPPrintWriter(String fileName, String csn) throws IOException {
        super(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(ExtensionManager.extension(fileName, "gz"))), csn));
    }

    public GZIPPrintWriter(String fileName) throws IOException {
        super(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(ExtensionManager.extension(fileName, "gz")))));
    }

}
