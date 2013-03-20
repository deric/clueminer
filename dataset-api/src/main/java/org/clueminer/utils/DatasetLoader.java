package org.clueminer.utils;

import java.io.File;
import java.io.FileNotFoundException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.exception.UnsupportedAttributeType;

/**
 *
 * @author Tomas Barton
 */
public interface DatasetLoader {
    
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
     * @throws UnsupportedAttributeType 
     */
    public boolean load(File file, Dataset output) throws FileNotFoundException, UnsupportedAttributeType;
}
