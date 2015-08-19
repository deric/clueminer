package org.clueminer.clustering.api;

import java.io.Serializable;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;

/**
 * Internal evaluator should not use information from labels or meta attributes.
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface InternalEvaluator<E extends Instance, C extends Cluster<E>> extends ClusterEvaluation<E, C>, Serializable {

    void setDistanceMeasure(Distance dm);

}
