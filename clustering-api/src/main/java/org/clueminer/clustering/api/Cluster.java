package org.clueminer.clustering.api;

import java.awt.Color;
import java.io.Serializable;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public interface Cluster<E extends Instance> extends Dataset<E>, Cloneable, Serializable {

    /**
     * Set cluster identification number
     *
     * @param id
     */
    public void setClusterId(int id);

    /**
     * Returns ID of the cluster
     *
     * @return id
     */
    public int getClusterId();

    /**
     * Color used for visualizations of clusters
     *
     * @return cluster's color
     */
    public Color getColor();

    /**
     * Set (usually) unique color for easier identification of the cluster
     *
     * @param color
     */
    public void setColor(Color color);

    /**
     * Centroids contains average value of all attributes in cluster
     *
     * @return usually non-existing element which is in the middle of the
     * cluster
     */
    public E getCentroid();

    /**
     * Counts number of identical elements in both clusters
     *
     * @param c
     * @return
     */
    public int countMutualElements(Cluster<E> c);
}
