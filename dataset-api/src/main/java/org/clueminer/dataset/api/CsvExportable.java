package org.clueminer.dataset.api;

import org.clueminer.utils.DatasetWriter;

/**
 *
 * @author Tomas Barton
 */
public interface CsvExportable {
    
    public void toCsv(DatasetWriter writer);

}
