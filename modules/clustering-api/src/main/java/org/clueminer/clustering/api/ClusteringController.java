package org.clueminer.clustering.api;

/**
 *
 * @author Tomas Barton
 */
public interface ClusteringController {

    public void clusterize(Clusterer clusterer);

    public void cancelClusterize(Clusterer clusterer);

    public void selectCluster(Cluster cluster);

    public void groupCluster(Cluster cluster);

    public void ungroupCluster(Cluster cluster);

    public boolean canGroup(Cluster cluster);

    public boolean canUngroup(Cluster cluster);
}