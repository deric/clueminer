package org.clueminer.clustering.api;

import java.io.Serializable;
import org.clueminer.distance.api.DistanceMeasure;

/**
 * External evaluation scores are measures which require information about true
 * classes (labels). It's mostly used for evaluation of clustering algorithms,
 * for real world problems you usually don't have information about class (from
 * the nature of an unsupervised learning)
 *
 * @author Tomas Barton
 */
public interface ExternalEvaluator extends ClusterEvaluation, Serializable {

    void setDistanceMeasure(DistanceMeasure dm);

    /**
     * We want to compare two clusterings to evaluate how similar they are
     *
     * @param c1
     * @param c2
     * @return
     */
    double score(Clustering<Cluster> c1, Clustering<Cluster> c2);

}
