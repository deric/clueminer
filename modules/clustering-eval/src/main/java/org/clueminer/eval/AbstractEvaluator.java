package org.clueminer.eval;

import java.util.Iterator;
import org.apache.commons.math3.util.FastMath;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.clueminer.stats.AttrNumStats;
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

    public double sumBetween(Clustering<? extends Cluster> clusters) {
        Cluster xc, yc;
        Instance x, y;
        double distance;
        double sum = 0.0;
        for (int i = 0; i < clusters.size(); i++) {
            xc = clusters.get(i);
            for (int m = 0; m < xc.size(); m++) {
                x = xc.instance(m);
                for (int j = 0; j < i; j++) {
                    yc = clusters.get(j);
                    for (int k = 0; k < yc.size(); k++) {
                        y = yc.instance(k);
                        distance = dm.measure(x, y);
                        if (!Double.isNaN(distance)) {
                            sum += distance;
                        }
                    }
                }
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

    /**
     * Sum of squared distance differences to the centroid of the cluster
     *
     * @param x
     * @return
     */
    public double sumOfSquaredError(Cluster<? extends Instance> x) {
        double squaredErrorSum = 0, dist;
        Instance centroid = x.getCentroid();
        for (Instance inst : x) {
            dist = dm.measure(inst, centroid);
            squaredErrorSum += FastMath.pow(dist, 2);
        }

        return squaredErrorSum;
    }

    /**
     * Variance of given attribute in the dataset
     *
     * @param clusters
     * @param d
     * @return
     */
    public double attrVar(Clustering<? extends Cluster> clusters, int d) {
        Dataset<? extends Instance> dataset = clusters.getLookup().lookup(Dataset.class);
        //variance for specific attribute - precomputed
        if (dataset != null) {
            return dataset.getAttribute(d).statistics(AttrNumStats.VARIANCE);
        }
        //compute variance manually
        double mu = attrMean(clusters, d);
        Iterator<Instance> iter = clusters.instancesIterator();
        Instance curr;
        double var = 0.0;
        int i = 0;
        while (iter.hasNext()) {
            curr = iter.next();
            var += FastMath.pow(mu - curr.get(d), 2);
            i++;
        }
        return var / (i - 1);
    }

    /**
     * Mean attribute value
     *
     * @param clusters
     * @param d        attribute index
     * @return
     */
    public double attrMean(Clustering<? extends Cluster> clusters, int d) {
        Dataset<? extends Instance> dataset = clusters.getLookup().lookup(Dataset.class);
        if (dataset != null) {
            return dataset.getAttribute(d).statistics(AttrNumStats.AVG);
        }
        Iterator<Instance> iter = clusters.instancesIterator();
        Instance curr;
        double mean = 0.0;
        int i = 0;
        while (iter.hasNext()) {
            curr = iter.next();
            mean += curr.get(d);
            i++;
        }
        return mean / i;
    }

    /**
     * With-in group squared scatter - distances between centroid.
     *
     * @param clusters
     * @return
     */
    public double wgss(Clustering<? extends Cluster> clusters) {
        double wgss = 0.0, dist;
        Cluster clust;
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            for (int j = 0; j < clust.size(); j++) {
                dist = dm.measure(clust.get(j), clust.getCentroid());
                wgss += dist * dist;
            }
        }
        return wgss;
    }

}
