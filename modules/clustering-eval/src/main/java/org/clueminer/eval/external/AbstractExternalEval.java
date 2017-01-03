/*
 * Copyright (C) 2011-2017 clueminer.org
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
