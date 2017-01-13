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
package org.clueminer.utils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.exception.ParserError;

/**
 * Imports data from file into Clueminer dataset representation.
 *
 * @author Tomas Barton
 * @param <E> data base type
 */
public interface DatasetLoader<E extends Instance> {

    /**
     * Convert input @param file into @param output. Dataset could not be empty
     * nor the inner implementation does matter. For sparse data, fixed numbers
     * attributes consider using dataset which performs best at given circumstances.
     *
     * @param file
     * @param output
     * @return
     * @throws IOException
     * @throws java.text.ParseException
     * @throws org.clueminer.exception.ParserError
     */
    boolean load(File file, Dataset<E> output) throws ParserError, ParseException, IOException;

    /**
     * Convert input @param file into @param output. Dataset could not be empty
     * nor the inner implementation does matter. For sparse data, fixed numbers
     * attributes consider using dataset which performs best at given circumstances.
     *
     * @param reader
     * @param output
     * @return
     * @throws IOException
     * @throws java.text.ParseException
     * @throws org.clueminer.exception.ParserError
     */
    boolean load(Reader reader, Dataset<E> output) throws ParserError, ParseException, IOException;
}
