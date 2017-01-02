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
package org.clueminer.chart.graphics;

import org.clueminer.chart.util.PointND;

/**
 * An interface for classes that want to be notified on navigation changes like
 * panning or zooming.
 *
 * @see Navigator
 */
public interface NavigationListener {

    /**
     * A method that gets called after the center of an object in the
     * {@code PlotNavigator} has changed.
     *
     * @param event An object describing the change event.
     */
    void centerChanged(NavigationEvent<PointND<? extends Number>> event);

    /**
     * A method that gets called after the zoom level of an object in the
     * {@code PlotNavigator} has changed.
     *
     * @param event An object describing the change event.
     */
    void zoomChanged(NavigationEvent<Double> event);
}
