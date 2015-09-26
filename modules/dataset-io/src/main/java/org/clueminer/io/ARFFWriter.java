/*
 * Copyright (C) 2011-2015 clueminer.org
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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author deric
 * @param <E>
 */
public class ARFFWriter<E extends Instance> implements Closeable, Flushable {

    private final Writer rawWriter;
    private final PrintWriter pw;
    private String lineEnd;
    private final String comment = "%";

    public static final String DEFAULT_LINE_END = "\n";

    public ARFFWriter(Writer writer) {
        this.rawWriter = writer;
        this.pw = new PrintWriter(writer);
        this.lineEnd = DEFAULT_LINE_END;
    }

    public void writeHeader(Dataset<E> dataset, String[] klassLabels) {
        StringBuilder sb = new StringBuilder();
        sb.append("@RELATION ").append(dataset.getName());
        writeLine(sb);
        writeLine(comment);

        Attribute attr;
        for (int i = 0; i < dataset.attributeCount(); i++) {
            attr = dataset.getAttribute(i);
            sb = new StringBuilder();
            sb.append("@ATTRIBUTE ");
            sb.append(attr.getName()).append(" ");
            switch (attr.getType().toString()) {
                case "NUMERICAL":
                case "NUMERIC":
                case "REAL":
                    sb.append("REAL");
                    break;
                default:
                    throw new RuntimeException("type '" + attr.getType().toString() + "'not supported");
            }
            writeLine(sb);
        }
        if (klassLabels != null) {
            sb = new StringBuilder("@ATTRIBUTE CLASS {");
            for (int i = 0; i < klassLabels.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(klassLabels[i]);
            }
            sb.append("}");
            writeLine(sb);
        }
        writeLine(comment);
        writeLine("@DATA");
    }

    public void write(E instance, String label) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < instance.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(String.valueOf(instance.value(i)));
        }
        if (label != null) {
            sb.append(",");
            sb.append(label);
        }
        writeLine(sb);
    }

    @Override
    public void close() throws IOException {
        flush();
        pw.close();
        rawWriter.close();
    }

    @Override
    public void flush() throws IOException {
        pw.flush();
    }

    public void writeLine(String line) {
        StringBuilder sb = new StringBuilder(line.length() + 2);
        writeLine(sb.append(line));
    }

    public void writeLine(StringBuilder sb) {
        sb.append(lineEnd);
        pw.write(sb.toString());
    }

}
