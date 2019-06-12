/*
 * Copyright (C) 2011-2019 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.api;

import java.util.Comparator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 * Interface is used to evaluate quality of clusterings
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface ClusterEvaluation<E extends Instance, C extends Cluster<E>> extends Comparator<Clustering<E, C>> {

    /**
     *
     * @return evaluation metric's name
     */
    String getName();

    /**
     * A DB-friendly name without spaces and special characters
     *
     * @return lower case short name
     */
    String getHandle();

    /**
     * Returns score for given clustering.
     *
     * @param clusters - clustering to be evaluated
     * @return criterion value obtained on this particular clustering
     * @throws org.clueminer.clustering.api.ScoreException
     */
    double score(Clustering<E, C> clusters) throws ScoreException;

    /**
     * Returns score for given clustering.
     *
     * @param clusters - clustering to be evaluated
     * @param params a HashMap with parameter settings (many criterion does not
     * take parameters)
     * @return criterion value obtained on this particular clustering
     * @throws org.clueminer.clustering.api.ScoreException
     */
    double score(Clustering<E, C> clusters, Props params) throws ScoreException;

    /**
     * Having proximity matrix can significantly improve efficiency of computing
     * scores, especially if multiple scores are evaluated
     *
     * @param clusters
     * @param proximity matrix of distances between all points
     * @param params optional parameters evaluation metric
     * @return
     * @throws org.clueminer.clustering.api.ScoreException
     */
    double score(Clustering<E, C> clusters, Matrix proximity, Props params) throws ScoreException;

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
     * When true: Arrays.sort(a) -> [10, 9, 8, ... ] When false: Arrays.sort(a)
     * -> [8, 9, 10]
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
