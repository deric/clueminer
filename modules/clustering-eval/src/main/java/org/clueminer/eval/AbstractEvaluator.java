package org.clueminer.eval;

import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.distance.api.DistanceMeasure;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractEvaluator implements InternalEvaluator, ClusterEvaluation {

    private static final long serialVersionUID = 6345948849700989503L;

    protected DistanceMeasure dm;
    protected double eps = 1e-8;

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

    /**
     * 0 when arguments are the same (within EPS range). 1 when score1 is bigger
     * (depends if we maximize or minimize score)
     * than score2. -1 when score1 is lower than score2. This behaviour is
     * inverse to Java defaults because we use descending order by default
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public int compare(double score1, double score2) {
        if (Double.isNaN(score1)) {
            score1 = replaceNaN(score1);
        }
        if (Double.isNaN(score2)) {
            score2 = replaceNaN(score2);
        }

        if (Math.abs(score1 - score2) < eps) {
            return 0;
        }
        if (isMaximized()) { //descending order [10, 9, 8, ...] (reversed order)
            if (score1 < score2) {
                return 1;
            }
        } else { // ascending order [1, 2, 3] (default Java order)
            if (score1 > score2) {
                return 1;
            }
        }
        return -1;
    }

    /**
     * NaN should be considered as the worst value
     *
     * @param v
     * @return
     */
    private double replaceNaN(double v) {
        if (isMaximized()) {
            return Double.MIN_VALUE;
        } else {
            return Double.MAX_VALUE;
        }
    }

}
