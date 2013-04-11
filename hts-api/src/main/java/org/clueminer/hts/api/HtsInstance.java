package org.clueminer.hts.api;

import java.io.Serializable;
import org.clueminer.dataset.api.ContinuousInstance;

/**
 *
 * @author Tomas Barton
 */
public interface HtsInstance extends Comparable, ContinuousInstance, Serializable {

    /**
     * Usually starting from 0
     *
     * @return row number
     */
    public int getRow();

    /**
     * Usually starts from 0, this number is typically converted to letters (for
     * bigger plates we need two letters, like indexing columns in a
     * spreadsheet)
     *
     * @return column number
     */
    public int getColumn();
}
