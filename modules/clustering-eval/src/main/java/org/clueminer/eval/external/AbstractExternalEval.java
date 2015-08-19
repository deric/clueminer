package org.clueminer.eval.external;

import java.io.Serializable;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.eval.AbstractComparator;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public abstract class AbstractExternalEval<E extends Instance, C extends Cluster<E>> extends AbstractComparator<E, C>
        implements ClusterEvaluation<E, C>, ExternalEvaluator<E, C>, Serializable {

    private static final long serialVersionUID = 7150802573224388450L;

    protected Distance dm;

    @Override
    public void setDistanceMeasure(Distance dm) {
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

}
