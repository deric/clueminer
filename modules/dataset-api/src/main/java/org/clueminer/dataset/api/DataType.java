package org.clueminer.dataset.api;

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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Various data could be supported by different backends and have different
 * storage requirements.
 *
 * @author deric
 */
public enum DataType {
    /**
     * discrete observations (several attributes)
     */
    DISCRETE,
    /**
     * 2 dependent variables
     */
    XY_CONTINUOUS,
    TIMESERIES
}
