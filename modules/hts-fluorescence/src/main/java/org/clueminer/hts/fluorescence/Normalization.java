package org.clueminer.hts.fluorescence;

import org.clueminer.hts.api.HtsInstance;
import org.clueminer.hts.api.HtsPlate;

/**
 *
 * @author Tomas Barton
 */
public abstract class Normalization {

    public abstract String getName();

    /**
     * Output dataset is passed as a reference
     *
     * @param plate
     * @param normalized
     */
    public abstract void normalize(HtsPlate<HtsInstance> plate, HtsPlate<HtsInstance> normalized);

    /**
     *
     * @param ord
     * @param col starts from 1, not zero!
     * @param colCnt
     * @return
     * @throws IOException
     */
    public int translatePosition(int ord, int col, int colCnt) {
        int res = ord * colCnt + col;
        return res;
    }
}
