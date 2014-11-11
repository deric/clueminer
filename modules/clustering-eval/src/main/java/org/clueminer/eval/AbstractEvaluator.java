package org.clueminer.eval;

import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.distance.api.DistanceMeasure;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractEvaluator implements InternalEvaluator, ClusterEvaluation {

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
    public int compareTo(double score1, double score2) {
        if (Double.isNaN(score1)) {
            score1 = replaceNaN(score1);
        }
        if (Double.isNaN(score2)) {
            score2 = replaceNaN(score2);
        }

        if (score1 == score2) {
            return 0;
        }
        if (isMaximized()) {
            if (score1 < score2) {
                return 1;
            }
        } else {
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
