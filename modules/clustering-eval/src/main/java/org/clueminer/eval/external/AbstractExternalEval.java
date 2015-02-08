package org.clueminer.eval.external;

import java.io.Serializable;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.distance.api.DistanceMeasure;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractExternalEval implements ClusterEvaluation, ExternalEvaluator, Serializable {

    private static final long serialVersionUID = 7150802573224388450L;

    protected DistanceMeasure dm;
    protected static final double eps = 1e-8;

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
        return true;
    }

    /**
     * Determines whether first score is better than the second
     *
     * @param score1
     * @param score2
     * @return true if score1 is better than score2
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        if (isMaximized()) {
            return score1 > score2;
        }
        return score1 < score2;
    }

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
        if (Double.isNaN(score1)) {
            score1 = replaceNaN(score1);
        }
        if (Double.isNaN(score2)) {
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
