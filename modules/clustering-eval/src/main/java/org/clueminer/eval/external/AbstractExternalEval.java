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
