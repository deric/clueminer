/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.api;

import java.io.Serializable;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;

/**
 * Internal evaluator should not use information from labels or meta attributes
 * for clustering evaluation.
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface InternalEvaluator<E extends Instance, C extends Cluster<E>> extends ClusterEvaluation<E, C>, Serializable {

    void setDistanceMeasure(Distance dm);

}
