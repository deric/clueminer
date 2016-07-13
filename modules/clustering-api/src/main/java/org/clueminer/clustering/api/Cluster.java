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

import java.awt.Color;
import java.io.Serializable;
import java.util.Set;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 * Cluster is a set of data points (instances {@link Instance}). Current
 * definition expect crisp clustering (each data point belong to a single cluster).
 *
 * @TODO fuzzy support
 *
 * @author Tomas Barton
 * @param <E> data row element
 */
public interface Cluster<E extends Instance> extends Dataset<E>, Cloneable, Serializable, Set<E> {

    /**
     * Set cluster identification number. Starts from 0, although cluster names
     * (for humans) should always start from 1.
     *
     * @param id
     */
    void setClusterId(int id);

    /**
     * Returns ID of the cluster (starts from 0)
     *
     * @return id
     */
    int getClusterId();

    /**
     * Color used for visualizations of clusters
     *
     * @return cluster's color
     */
    Color getColor();

    /**
     * Set (usually) unique color for easier identification of the cluster
     *
     * @param color
     */
    void setColor(Color color);

    /**
     * Centroids contains average value of all attributes in cluster
     *
     * @return usually non-existing element which is in the middle of the
     *         cluster
     */
    E getCentroid();

    /**
     * In some algorithms we can easily update centroid and thus speedup the
     * clustering process
     *
     * @param centroid
     */
    void setCentroid(E centroid);

    /**
     * Counts number of identical elements in both clusters
     *
     * @param c
     * @return
     */
    int countMutualElements(Cluster<E> c);

    /**
     * Add element to cluster and marks original id (position) in input matrix,
     * which could be checked if it is contained in cluster
     *
     * @param inst
     * @param origId
     */
    //public void add(E inst, int origId);
    /**
     * Checks presence of element by original id (position in input matrix)
     *
     * @param origId
     * @return true when element present
     */
    boolean contains(int origId);

    /**
     * By default return false (density based algorithms label typically last
     * cluster as outliers)
     *
     * @return true when cluster consists of outliers (noisy data points)
     */
    boolean isNoise();
}
