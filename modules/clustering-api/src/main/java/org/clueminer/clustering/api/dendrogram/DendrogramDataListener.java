/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.clustering.api.dendrogram;

import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */
public interface DendrogramDataListener extends EventListener {

    void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset);

    /**
     *
     * @param evt
     * @param width       new element width
     * @param isAdjusting when true user is changing the value with some slider,
     *                    so we should draw the result as fast as possible
     */
    void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting);

    /**
     *
     * @param evt
     * @param height
     * @param isAdjusting when true user is changing the value with some slider,
     *                    so we should draw the result as fast as possible
     */
    void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting);
}
