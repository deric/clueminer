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
     * @param col    starts from 1, not zero!
     * @param colCnt
     * @return
     * @throws IOException
     */
    public int translatePosition(int ord, int col, int colCnt) {
        int res = ord * colCnt + col;
        return res;
    }
}
