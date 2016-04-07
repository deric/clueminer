package org.clueminer.utils;

import java.io.File;
import java.io.FileNotFoundException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

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
     * attributes consider using dataset which performs best at given
     * circumstances
     *
     * @param file
     * @param output
     * @return
     * @throws FileNotFoundException
     */
    boolean load(File file, Dataset<E> output) throws FileNotFoundException;
}
