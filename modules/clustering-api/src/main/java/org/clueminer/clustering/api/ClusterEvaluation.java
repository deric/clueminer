package org.clueminer.clustering.api;

import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 * Interface is used to evaluate quality of clusterings
 *
 * @author Tomas Barton
 */
public interface ClusterEvaluation {

    /**
     *
     * @return evaluation metric's name
     */
    String getName();

    /**
     * Returns score for given clustering.
     *
     * @param clusters - clustering to be evaluated
     * @return criterion value obtained on this particular clustering
     */
    double score(Clustering<? extends Cluster> clusters);

    /**
     * Returns score for given clustering.
     *
     * @param clusters - clustering to be evaluated
     * @param params a HashMap with parameter settings (many criterion does not
     * take parameters)
     * @return criterion value obtained on this particular clustering
     */
    double score(Clustering<? extends Cluster> clusters, Props params);

    /**
     * Having proximity matrix can significantly improve efficiency of computing
     * scores, especially if multiple scores are evaluated
     *
     * @param clusters
     * @param proximity matrix of distances between all points
     * @param params optional parameters evaluation metric
     * @return
     */
    double score(Clustering<? extends Cluster> clusters, Matrix proximity, Props params);

    /**
     * Compares the two scores according to the criterion in the implementation.
     * Some score should be maximized, others should be minimized. This method
     * returns true if the first score is 'better' than the second score.
     *
     * @param score1 - the first score
     * @param score2 - the second score
     * @return true if the first score is better than the second, false in all
     * other cases
     */
    boolean isBetter(double score1, double score2);

    /**
     * Classical C-like comparator, return 0 when scores are equal, 1 when
     * score1 is better than score2, -1 otherwise.
     *
     * @param score1
     * @param score2
     * @return
     */
    int compare(double score1, double score2);

    /**
     *
     *
     * @return true when class labels are required in order to evaluate score
     */
    boolean isExternal();

    /**
     * Value is used for sorting results.
     *
     * When true: Arrays.sort(a) -> [10, 9, 8, ... ]
     * When false: Arrays.sort(a) -> [8, 9, 10]
     *
     * @return true when bigger is better
     */
    boolean isMaximized();

    /**
     * Minimal (worst) value of this index
     *
     * @return
     */
    double getMin();

    /**
     * Maximal (best) value of this index
     *
     * @return
     */
    double getMax();
}
