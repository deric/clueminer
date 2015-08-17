package org.clueminer.clustering.api;

import java.io.Serializable;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;

/**
 * External evaluation scores are measures which require information about true
 * classes (labels). It's mostly used for evaluation of clustering algorithms,
 * for real world problems you usually don't have information about class (from
 * the nature of an unsupervised learning)
 *
 * @author Tomas Barton
 */
public interface ExternalEvaluator extends ClusterEvaluation, Serializable {

    void setDistanceMeasure(Distance dm);

    /**
     * Returns score for given clustering compared to class labels. The original
     * dataset set can be obtained from lookup of the clustering.
     *
     * @param clusters - clustering to be evaluated
     * @param params
     * @return criterion value obtained on this particular clustering
     */
    @Override
    double score(Clustering<? extends Cluster> clusters, Props params);

    /**
     * We want to compare two clusterings to evaluate how similar they are
     *
     * @param c1
     * @param c2
     * @param params
     * @return
     */
    double score(Clustering<Cluster> c1, Clustering<Cluster> c2, Props params);

}
