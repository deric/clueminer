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

/**
 * Data class that describes a navigational event, like zooming or panning.
 *
 * @param <T> Data type of the value that has been changed.
 */
public class NavigationEvent<T> {

    /**
     * Object that has caused the change.
     */
    private final Navigator source;
    /**
     * Value before the change.
     */
    private final T valueOld;
    /**
     * Value after the change.
     */
    private final T valueNew;

    /**
     * Initializes a new instance.
     *
     * @param source   Navigator object that has caused the change.
     * @param valueOld Value before the change
     * @param valueNew Value after the change.
     */
    public NavigationEvent(Navigator source, T valueOld, T valueNew) {
        this.source = source;
        this.valueOld = valueOld;
        this.valueNew = valueNew;
    }

    /**
     * Returns the navigator that has caused the change.
     *
     * @return Navigator object that has caused the change.
     */
    public Navigator getSource() {
        return source;
    }

    /**
     * Returns the value before the change.
     *
     * @return Value before the change.
     */
    public T getValueOld() {
        return valueOld;
    }

    /**
     * Returns the value after the change.
     *
     * @return Value after the change.
     */
    public T getValueNew() {
        return valueNew;
    }
}
