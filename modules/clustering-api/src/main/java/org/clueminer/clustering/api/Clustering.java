package org.clueminer.clustering.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.openide.util.Lookup;

/**
 * Clustering is a set of clusters where each of clusters have to implement
 * Dataset interface.
 *
 * @author Tomas Barton
 * @param <C>
 * @param <E>
 */
public interface Clustering<E extends Instance, C extends Cluster<E>> extends Cloneable, Serializable, Iterable<C>, Collection<C> {

    /**
     * ID is usually assigned by algorithms generating many different
     * clusterings.
     *
     * @return order id
     */
    int getId();

    /**
     * Set order ID for given clustering
     *
     * @param id
     */
    void setId(int id);

    /**
     * Human readable name of the clustering
     *
     * @return basic information about clustering
     */
    String getName();

    /**
     * Sets name for the clustering
     *
     * @param name
     */
    void setName(String name);

    /**
     * @return number of clusters in clustering
     */
    @Override
    int size();

    /**
     * Get i-th item
     *
     * @param i
     * @return
     */
    C get(int i);

    /**
     * Find cluster by label, nil if given label is not present
     *
     * @param label
     * @return cluster with given label
     */
    C get(String label);

    /**
     * Inserts Dataset (a cluster) into Clustering (a set of clusters)
     *
     * @param d
     */
    void put(C d);

    /**
     * Add element to collection
     *
     * @param e
     * @return
     */
    @Override
    boolean add(C e);

    /**
     * Inserts Cluster at i-th position
     *
     * @param index
     * @param d
     */
    void put(int index, C d);

    /**
     * Return true if Dataset exists at index
     *
     * @param index
     * @return
     */
    boolean hasAt(int index);

    /**
     * Merge all given Datasets into the first one
     *
     * @param datasets
     */
    void merge(C... datasets);

    /**
     * Name of i-th cluster
     *
     * @param i
     * @return label of cluster (e.g. number as string)
     */
    String getClusterLabel(int i);

    /**
     * In case of hard assignments should be equal to total number of elements
     * (instances) in the original dataset
     *
     * @return total number of elements (in all clusters)
     */
    int instancesCount();

    /**
     * Iterator over all instances in clustering regardless assignment to a
     * cluster
     *
     * @return instances iterator
     */
    Iterator<E> instancesIterator();

    /**
     * Computes centroid for the whole dataset (clustering)
     *
     * @return centroid for all clusters
     */
    E getCentroid();

    /**
     * Instances are numbered from 0 to {@code instancesCount()-1}
     *
     * @param i
     * @return i-th instance in the clustering
     */
    E instance(int i);

    /**
     *
     * @return sizes of all clusters
     */
    int[] clusterSizes();

    /**
     * Return ID of item's cluster
     *
     * @param instanceId
     * @return
     */
    int assignedCluster(int instanceId);

    /**
     *
     * @param inst
     * @return cluster to which is {@code inst} assigned
     */
    C assignedCluster(E inst);

    /**
     * Create new cluster on given index
     *
     * @param clusterIndex index starts from 0 unlike cluster ID (from 1)
     * @return newly created cluster
     */
    C createCluster(int clusterIndex);

    /**
     * Create cluster with new ID (starting from 1)
     *
     * @return
     */
    C createCluster();

    /**
     * Create new cluster with given ID and initial capacity
     *
     * @param clusterIndex index starts from 0 unlike cluster ID (from 1)
     * @param capacity cluster capacity
     * @return newly created cluster
     */
    C createCluster(int clusterIndex, int capacity);

    /**
     * Create new cluster with given capacity and name
     *
     * @param clusterIndex
     * @param capacity
     * @param name
     * @return
     */
    C createCluster(int clusterIndex, int capacity, String name);

    /**
     * Set name of cluster at given index
     *
     * @param clusterIndex target cluster
     * @param name new name
     */
    void setClusterName(int clusterIndex, String name);

    /**
     * Lookup is used for retrieving objects associated with this clustering
     * result
     *
     * @return lookup instance for accessing related objects (Dataset,
     * hierarchical clustering etc.)
     */
    Lookup getLookup();

    /**
     * Add object to lookup
     *
     * @param instance
     */
    void lookupAdd(Object instance);

    /**
     * Removes object from lookup
     *
     * @param instance
     */
    void lookupRemove(Object instance);

    /**
     * Parameters which were used to compute clustering
     *
     * @return
     */
    Props getParams();

    /**
     * Sets parameters which were used to obtain clustering
     *
     * @param params
     */
    void setParams(Props params);

    void mergeParams(Props params);

    /**
     * By default evaluation table is null
     *
     * @return structure for storing evaluation results
     */
    EvaluationTable<E, C> getEvaluationTable();

    /**
     * Set structure (hash-map) for storing results
     *
     * @param table
     */
    void setEvaluationTable(EvaluationTable<E, C> table);

    /**
     * Structure fingerprint, clustering with the same fingerprint does not have
     * to be the same but should be very similar
     *
     * @return description of cluster's structure
     */
    String fingerprint();

    /**
     * Remove empty clusters, might relabel existing clusters
     */
    void compact();

    /**
     * Return value of a metric if it was already pre-computed, otherwise
     * Double.NaN
     *
     * @param metric
     * @return
     */
    double getValidation(String metric);

    /**
     * Check for presence of a validation metric
     *
     * @param metric
     * @return true when given metric was already pre-computed
     */
    boolean hasValidation(String metric);

    /**
     * Set validation metric (or just computationally expensive part of the
     * metric)
     *
     * @param metric
     * @param value
     */
    void setValidation(String metric, double value);

    /**
     * Remove cluster on given index
     *
     * @param idx
     * @return true when successful
     */
    boolean remove(int idx);

    /**
     * Get cluster with outliers
     *
     * @return cluster containing noisy data points
     */
    C getNoise();
}
