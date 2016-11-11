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
package org.clueminer.io.csv;

import java.io.IOException;
import java.util.Iterator;

public class CSVIterator implements Iterator<String[]> {

    private final CSVReader reader;
    private String[] nextLine;

    public CSVIterator(CSVReader reader) throws IOException {
        this.reader = reader;
        nextLine = reader.readNext();
    }

    @Override
    public boolean hasNext() {
        return nextLine != null;
    }

    @Override
    public String[] next() {
        String[] temp = nextLine;
        try {
            nextLine = reader.readNext();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return temp;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("This is a read only iterator.");
    }

    public String[] showNext() {
        return nextLine;
    }
}
