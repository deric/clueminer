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
package org.clueminer.clustering.api;

import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.netbeans.api.progress.ProgressHandle;

/**
 * General clustering algorithm interface
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface ClusteringAlgorithm<E extends Instance, C extends Cluster<E>> {

    String getName();

    /**
     * Cluster given dataset
     *
     * @param dataset
     * @param props   a set of parameter that influence clustering or
     *                performance
     * @return
     */
    Clustering<E, C> cluster(Dataset<E> dataset, Props props);

    Distance getDistanceFunction();

    void setDistanceFunction(Distance dm);

    /**
     * Algorithm responsible for assigning colors to new clusters
     *
     * @param cg
     */
    void setColorGenerator(ColorGenerator cg);

    /**
     *
     * @return
     */
    ColorGenerator getColorGenerator();

    /**
     * API for displaying progress in UI, if not set algorithm should work
     * anyway
     *
     * @param ph
     */
    void setProgressHandle(ProgressHandle ph);

    Parameter[] getParameters();

    /**
     * Default configurator for this algorithm.
     *
     * @return parameter estimator
     */
    Configurator<E> getConfigurator();
}
