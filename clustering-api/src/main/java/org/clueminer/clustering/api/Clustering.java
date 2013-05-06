package org.clueminer.clustering.api;

import java.io.Serializable;
import java.util.Collection;
import org.clueminer.dataset.api.Instance;

/**
 * Clustering is a set of clusters where each of clusters have to implement
 * Dataset interface.
 *
 * @author Tomas Barton
 */
public interface Clustering<T extends Cluster> extends Cloneable, Serializable, Iterable<T>, Collection<T> {

    /**
     * @return number of clusters in clustering
     */
    @Override
    public int size();

    /**
     * Get i-th item
     *
     * @param i
     * @return
     */
    public T get(int i);

    /**
     * Inserts Dataset (a cluster) into Clustering (a set of clusters)
     *
     * @param d
     */
    public void put(Cluster<Instance> d);

    /**
     * Inserts Dataset at i-th position
     *
     * @param index
     * @param d
     */
    public void put(int index, Cluster<Instance> d);

    /**
     * Return true if Dataset exists at index
     *
     * @param index
     * @return
     */
    public boolean hasAt(int index);

    /**
     * Merge all given Datasets into the first one
     *
     * @param datasets
     */
    public void merge(Cluster<Instance>... datasets);

    /**
     * Name of i-th cluster
     *
     * @param i
     * @return label of cluster (e.g. number as string)
     */
    public String getClusterLabel(int i);

    /**
     * In case of hard assignments should be equal to total number of elements
     * (instances) in the original dataset
     *
     * @return total number of elements (in all clusters)
     */
    public int instancesCount();

    /**
     * Computes centroid for the whole dataset (clustering)
     *
     * @return centroid for all clusters
     */
    public Instance getCentroid();
}
