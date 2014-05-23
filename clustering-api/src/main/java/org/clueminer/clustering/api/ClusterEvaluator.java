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
}
