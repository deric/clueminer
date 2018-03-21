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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

/**
 * A class to iterate over all columns in a file.
 *
 * @author Thomas Abeel
 *
 */
public class ColumnIterator implements Iterable<String[]>, Iterator<String[]>, Closeable {

    private BufferedReader in = null;

    private String next = null;

    public ColumnIterator(String file) {
        this(new File(file));
    }

    public ColumnIterator(File f) {
        try {
            in = new BufferedReader(new FileReader(f));
            next = in.readLine();
            if (next == null) {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean skipBlanks = false;

    private boolean skipComments = false;

    private String commentIdentifier = "#";

    private String delim = "\t";

    public ColumnIterator(InputStream stream) {
        this(new InputStreamReader(stream));
    }

    public ColumnIterator(Reader reader) {
        try {
            in = new BufferedReader(reader);
            next = in.readLine();
            if (next == null) {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterator<String[]> iterator() {
        return this;
    }

    public boolean hasNext() {
        return next != null;
    }

    private void getNext() {
        try {
            next = in.readLine();
            while (next != null
                    && ((skipBlanks && next.length() == 0) || (skipComments && next.startsWith(commentIdentifier)))) {
                next = in.readLine();
            }

            if (next == null) {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] next() {
        String oldNext = next;

        getNext();
        return oldNext.split(delim);

    }

    public void remove() {
        throw new UnsupportedOperationException("This operation is not possible on a ColumnIterator");

    }

    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            //Be silent
        }
        next = null;

    }

    /**
     * Sets whether blank lines should be skipped.
     *
     * @param skipBlanks
     */
    public final void setSkipBlanks(boolean skipBlanks) {
        this.skipBlanks = skipBlanks;
        if (next != null && this.skipBlanks && next.length() == 0) {
            getNext();
        }
    }

    /**
     * Sets whether blank comment lines should be skipped. Comments lines are
     * those lines that start with the commentIdentifier.
     *
     * @param skipComments
     */
    public final void setSkipComments(boolean skipComments) {
        this.skipComments = skipComments;
        if (this.skipComments && next != null && next.startsWith(commentIdentifier)) {
            getNext();
        }
    }

    public final void setCommentIdentifier(String commentIdentifier) {
        this.commentIdentifier = commentIdentifier;
        if (next != null && this.skipComments && next.startsWith(commentIdentifier)) {
            getNext();
        }
    }

    public final void setDelimiter(String delim) {
        this.delim = delim;
    }

}
