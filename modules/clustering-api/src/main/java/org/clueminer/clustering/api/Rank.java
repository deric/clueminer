/*
 * Copyright (C) 2011-2019 clueminer.org
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

import java.util.Comparator;
import java.util.List;
import org.clueminer.clustering.api.config.ConfigException;
import org.clueminer.dataset.api.Instance;

/**
 * API for ranking clusterings according to provided evaluation measures.
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public interface Rank<E extends Instance, C extends Cluster<E>> {

    /**
     * Method identification
     *
     * @return the name
     */
    String getName();

    /**
     * Sort given list using provided objective(s)
     *
     * @param clusterings
     * @param objectives
     * @return
     */
    Clustering<E, C>[] sort(Clustering<E, C>[] clusterings, List<ClusterEvaluation<E, C>> objectives);

    /**
     * Whether ranking requires multiple objectives
     *
     * @return
     */
    boolean isMultiObjective();

    /**
     * Comparator used for sorting/ranking.
     *
     * @return clustering comparator
     */
    Comparator<Clustering<E, C>> getComparator();

    /**
     *
     * @return objective(s) used for evaluation
     */
    ClusterEvaluation<E, C> getEvaluator();

    void validate(List<ClusterEvaluation<E, C>> objectives) throws ConfigException;

}
