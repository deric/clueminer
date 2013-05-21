package org.clueminer.clustering.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.math.Matrix;

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
    public String getName();

    /**
     * Returns the score the current clusterer obtains on the dataset.
     *
     * @param d - the dataset to test the clusterer on.
     * @param dataset - the original dataset
     * @return the score the clusterer obtained on this particular dataset
     */
    public double score(Clustering clusters, Dataset dataset);

    /**
     * Having proximity matrix can significantly improve efficiency of computing
     * scores, especially if multiple scores are evaluated
     *
     * @param clusters
     * @param dataset - the original dataset
     * @param proximity matrix of distiances between all points
     * @return
     */
    public double score(Clustering clusters, Dataset dataset, Matrix proximity);

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
    public boolean compareScore(double score1, double score2);
}