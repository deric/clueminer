/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.graph.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.utils.Props;

/**
 * Interface for converting basic spreadsheet data to a graph structure
 *
 * @author deric
 */
public interface GraphConvertor {

    /**
     * Unique identifier
     *
     * @return name of the method
     */
    String getName();

    /**
     * Add egdes to the graph based on dataset similarity
     *
     * @param graph
     * @param dataset
     * @param mapping
     * @param params
     */
    void createEdges(Graph graph, Dataset<? extends Instance> dataset, Long[] mapping, Props params);

    /**
     * Function for measuring distance between data points
     *
     * @param dm
     */
    void setDistanceMeasure(DistanceMeasure dm);
}
