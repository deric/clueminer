package org.clueminer.hts.api;

import java.io.Serializable;
import org.clueminer.dataset.api.ContinuousInstance;

/**
 *
 * @author Tomas Barton
 */
public interface HtsInstance<T extends Number> extends Comparable, ContinuousInstance<T>, Serializable {

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

    /**
     * Return true is there is chemical formula in any format
     *
     * @return true is any chemical structure available
     */
    public boolean hasFormula();

    /**
     * Chemical structure in smilies format
     *
     * @return string in smilies format
     */
    public String getSmiliesFormula();
}
