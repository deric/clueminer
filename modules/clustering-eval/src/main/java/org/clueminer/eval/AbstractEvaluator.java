package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractEvaluator extends AbstractComparator implements InternalEvaluator, ClusterEvaluation {

    private static final long serialVersionUID = 6345948849700989503L;

    protected DistanceMeasure dm;

    @Override
    public void setDistanceMeasure(DistanceMeasure dm) {
        this.dm = dm;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public boolean isExternal() {
        return false;
    }

    @Override
    public double score(Clustering clusters) {
        return score(clusters, new Props());
    }

    @Override
    public double score(Clustering clusters, Matrix proximity, Props params) {
        return score(clusters, params);
    }

    /**
     * Sum of distances within the cluster
     *
     * @param cluster
     * @return
     */
    public double sumWithin(Cluster<? extends Instance> cluster) {
        double sum = 0.0;
        Instance x, y;
        for (int i = 0; i < cluster.size(); i++) {
            x = cluster.instance(i);
            for (int j = 0; j < i; j++) {
                y = cluster.instance(j);
                sum += dm.measure(x, y);
            }
        }

        return sum;
    }

    /**
     * Number of within-cluster pairs
     *
     * @param clusters
     * @return
     */
    public int numW(Clustering<? extends Cluster> clusters) {
        int numWPairs = 0;
        //number of within pairs
        for (Cluster clust : clusters) {
            numWPairs += clust.size() * clust.size();
        }
        return (numWPairs - clusters.instancesCount()) >>> 1; // (numWpairs - N) / 2
    }

    /**
     * Number of cluster pairs in the whole dataset
     *
     * @param clusters
     * @return
     */
    public int numT(Clustering<? extends Cluster> clusters) {
        int n = clusters.instancesCount();
        return (n * (n - 1)) >>> 1; // (numWpairs - N) / 2
    }

}
