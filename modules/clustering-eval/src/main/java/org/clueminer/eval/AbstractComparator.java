package org.clueminer.eval;

import org.clueminer.clustering.api.ClusterEvaluation;

/**
 * Ensures comparing between standard Double values and not finite values (NaN,
 * positive infinity, negative infinity)
 *
 * @author Tomas Barton
 */
public abstract class AbstractComparator implements ClusterEvaluation {

    protected static final double eps = 1e-8;

    /**
     * 0 when arguments are the same (within EPS range). 1 when score1 is worser
     * than score2. -1 when score1 is better than score2. This behaviour is
     * inverse to Java defaults because we use descending order by default
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public int compare(double score1, double score2) {
        if (!isFinite(score1)) {
            score1 = replaceNaN(score1);
        }
        if (!isFinite(score2)) {
            score2 = replaceNaN(score2);
        }

        if (Math.abs(score1 - score2) < eps) {
            return 0;
        }
        if (isMaximized()) { //descending order [10, 9, 8, ...]
            if (score1 < score2) {
                return 1;
            }
        } else { // ascending order [1, 2, 3]
            if (score1 > score2) {
                return 1;
            }
        }
        return -1;
    }

    /**
     * NaN should be considered as the worst value. Replaces NaN and infinity
     * values.
     *
     * @param v
     * @return
     */
    private double replaceNaN(double v) {
        if (Double.isNaN(v)) {
            if (isMaximized()) {
                return Double.MIN_VALUE;
            } else {
                return Double.MAX_VALUE;
            }
        } else {
            if (v == Double.NEGATIVE_INFINITY) {
                return Double.MIN_VALUE;
            } else { // POSITIVE_INFINITY
                return Double.MAX_VALUE;
            }
        }
    }

    /**
     * Could be replace by Double.isFinite which is available in Java 8
     *
     * @param d
     * @return
     */
    public boolean isFinite(double d) {
        return Math.abs(d) <= Double.MAX_VALUE;
    }

}
