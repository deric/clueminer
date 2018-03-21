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
package org.clueminer.chart.data;

/**
 * Interface that can be implemented to listen for changes in data sources.
 *
 * @see DataSource
 */
public interface DataListener {

    /**
     * Method that is invoked when data has been added.
     * This method is invoked by objects that provide support for
     * {@code DataListener}s and should not be called manually.
     *
     * @param source Data source that has been changed.
     * @param events Optional event object describing the data values that
     *               have been added.
     */
    void dataAdded(DataSource source, DataChangeEvent... events);

    /**
     * Method that is invoked when data has been updated.
     * This method is invoked by objects that provide support for
     * {@code DataListener}s and should not be called manually.
     *
     * @param source Data source that has been changed.
     * @param events Optional event object describing the data values that
     *               have been updated.
     */
    void dataUpdated(DataSource source, DataChangeEvent... events);

    /**
     * Method that is invoked when data has been removed.
     * This method is invoked by objects that provide support for
     * {@code DataListener}s and should not be called manually.
     *
     * @param source Data source that has been changed.
     * @param events Optional event object describing the data values that
     *               have been removed.
     */
    void dataRemoved(DataSource source, DataChangeEvent... events);
}
