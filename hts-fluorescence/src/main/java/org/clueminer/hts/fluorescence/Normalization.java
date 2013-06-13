package org.clueminer.hts.fluorescence;

import org.clueminer.dataset.api.Dataset;

/**
 *
 * @author Tomas Barton
 */
public abstract class Normalization {

    public abstract String getName();

    public abstract Dataset<FluorescenceInstance> normalize(FluorescenceDataset plate);

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
