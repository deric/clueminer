package org.clueminer.clustering.api;

/**
 *
 * @author Tomas Barton
 */
public interface Clusterer {

    public void execute();

    public Cluster[] getClusters();
}
