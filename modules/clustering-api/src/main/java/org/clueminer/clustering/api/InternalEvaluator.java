package org.clueminer.clustering.api;

import java.io.Serializable;
import org.clueminer.distance.api.Distance;

/**
 * Internal evaluator should not use information from labels or meta attributes.
 *
 * @author Tomas Barton
 */
public interface InternalEvaluator extends ClusterEvaluation, Serializable {

    void setDistanceMeasure(Distance dm);

}
