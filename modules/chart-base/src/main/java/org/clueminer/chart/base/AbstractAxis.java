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
package org.clueminer.chart.base;

import java.io.Serializable;
import java.util.List;
import org.clueminer.chart.api.AbstractDrawable;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.AxisListener;
import org.clueminer.chart.api.AxisRenderer;
import org.clueminer.chart.api.Plot;
import org.clueminer.chart.util.Orientation;

/**
 *
 * @author deric
 */
public abstract class AbstractAxis extends AbstractDrawable implements Axis, Serializable {

    /**
     * Has the axis a valid range. Used for auto-scaling.
     */
    protected boolean autoscaled;

    /**
     * Objects that will be notified when axis settings are changing.
     */
    protected transient List<AxisListener> axisListeners;

    protected AxisRenderer renderer;

    protected Orientation orientation;

    protected Plot plot;

    /**
     * Adds the specified {@code AxisListener} to this Axis. The Listeners will
     * be notified if changes to the Axis occur, for Example if the minimum or
     * maximum value changes.
     *
     * @param listener Listener to be added
     * @see AxisListener
     */
    @Override
    public void addAxisListener(AxisListener listener) {
        axisListeners.add(listener);
    }

    /**
     * Removes the specified {@code AxisListener} from this Axis.
     *
     * @param listener Listener to be removed
     * @see AxisListener
     */
    @Override
    public void removeAxisListener(AxisListener listener) {
        axisListeners.remove(listener);
    }

    /**
     * Notifies all registered {@code AxisListener}s that the value range has
     * changed.
     *
     * @param min new minimum value
     * @param max new maximum value
     */
    @Override
    public void fireRangeChanged(Number min, Number max) {
        for (AxisListener listener : axisListeners) {
            listener.rangeChanged(this, min, max);
        }
    }

    /**
     * Returns whether the axis range should be determined automatically rather
     * than using the axis's minimum and a maximum values.
     *
     * @return whether the axis is scaled automatically to fit the current data
     */
    @Override
    public boolean isAutoscaled() {
        return autoscaled;
    }

    /**
     * Sets whether the axis range should be determined automatically rather
     * than using the axis's minimum and a maximum values.
     *
     * @param autoscaled Defines whether the axis should be automatically scaled
     *                   to fit the current data.
     */
    @Override
    public void setAutoscaled(boolean autoscaled) {
        if (this.autoscaled != autoscaled) {
            this.autoscaled = autoscaled;
        }
    }

    /**
     *
     * @return
     */
    @Override
    public AxisRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void setRenderer(AxisRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public Plot getPlot() {
        return plot;
    }

}
