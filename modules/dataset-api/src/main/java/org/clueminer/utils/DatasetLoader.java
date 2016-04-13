package org.clueminer.utils;

import java.io.File;
import java.io.FileNotFoundException;
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
     * @throws FileNotFoundException
     * @throws org.clueminer.exception.ParserError
     */
    boolean load(File file, Dataset<E> output) throws FileNotFoundException, ParserError, ParseException;

    /**
     * Convert input @param file into @param output. Dataset could not be empty
     * nor the inner implementation does matter. For sparse data, fixed numbers
     * attributes consider using dataset which performs best at given circumstances.
     *
     * @param reader
     * @param output
     * @return
     * @throws FileNotFoundException
     * @throws org.clueminer.exception.ParserError
     */
    boolean load(Reader reader, Dataset<E> output) throws FileNotFoundException, ParserError, ParseException;
}
