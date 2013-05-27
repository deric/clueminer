package org.clueminer.clustering.api;

import java.io.Serializable;
import org.clueminer.distance.api.DistanceMeasure;

/**
 * External evaluation scores are measures which require information about true
 * classes (labels). It's mostly used for evaluation of clustering algorithms,
 * for real world problems you usually don't have information about class (from
 * nature of unsupervised learning)
 *
 * @author Tomas Barton
 */
public abstract class ExternalEvaluator implements ClusterEvaluation, Serializable {

    private static final long serialVersionUID = 7854838110957041190L;
    protected DistanceMeasure dm;

    public void setDistanceMeasure(DistanceMeasure dm) {
        this.dm = dm;
    }

    /**
     * We want to compare two clusterings to evaluate how similar they are
     *
     * @param c1
     * @param c2
     * @return
     */
    public abstract double score(Clustering<Cluster> c1, Clustering<Cluster> c2);
}
