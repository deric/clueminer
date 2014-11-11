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

}
