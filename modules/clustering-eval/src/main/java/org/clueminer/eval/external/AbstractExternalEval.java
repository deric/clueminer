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

}
