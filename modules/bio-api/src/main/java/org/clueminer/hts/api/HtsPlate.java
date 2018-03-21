/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.hts.api;

import java.util.Collection;
import java.util.Set;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Timeseries;

/**
 * High-throughput screening plate
 *
 * Plates for screening have rectagonal shape, typically we have plates with 96,
 * 384 or 1536 wells
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface HtsPlate<E extends HtsInstance> extends Timeseries<E>, Dataset<E>, Collection<E>, Set<E> {

    /**
     * Plate unique identification in database
     *
     * @return unique ID
     */
    @Override
    public String getId();

    /**
     *
     * @return number of rows
     */
    public int getRowsCount();

    /**
     * Columns are usually marked with alphabetic letters, typically we have 12,
     * 24 or 32 columns
     *
     * @return number of columns
     */
    public int getColumnsCount();

    public void setRowsCount(int rows);

    public void setColumnsCount(int cols);
}
