package org.clueminer.clustering.api;

import java.io.Serializable;
import org.clueminer.distance.api.DistanceMeasure;

/**
 *
 * @author Tomas Barton
 */
public abstract class ClusterEvaluator implements ClusterEvaluation, Serializable {

    private static final long serialVersionUID = 1947286273667041934L;
    protected DistanceMeasure dm;

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
