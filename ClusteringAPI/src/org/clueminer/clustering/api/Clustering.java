package org.clueminer.clustering.api;

import java.io.Serializable;
import org.clueminer.dataset.Dataset;
import org.clueminer.instance.Instance;

/**
 * Clustering is a set of clusters where each of clusters have to implement
 * Dataset interface. 
 * 
 * @author Tomas Barton
 */
public interface Clustering extends Iterable<Dataset<Instance>>, Cloneable, Serializable {
    
    /**
     * @return number of clusters in clustering
     */
    public int size();
    
    /**
     * 
     * @param i index of cluster, starts from 0
     * @return i-th dataset
     */
    public Dataset<Instance> get(int i);
    
    /**
     * Inserts Dataset (a cluster) into Clustering (a set of clusters)
     * @param d 
     */
    public void put(Dataset<Instance> d);
    
    /**
     * Inserts Dataset at i-th position
     * @param index
     * @param d 
     */
    public void put(int index, Dataset<Instance> d);
    
    /**
     * Return true if Dataset exists at index
     * @param index
     * @return 
     */
    public boolean hasAt(int index);
    
    /**
     * Merge all given Datasets into the first one
     * 
     * @param datasets 
     */
    public void merge(Dataset<Instance>... datasets);
    
    /**
     * Name of i-th cluster
     * @param i
     * @return label of cluster (e.g. number as string)
     */
    public String getClusterLabel(int i);

}
