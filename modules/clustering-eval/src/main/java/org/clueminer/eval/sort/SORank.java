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
package org.clueminer.eval.sort;

import java.util.Arrays;
import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Rank;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.ClusteringComparator;
import org.openide.util.lookup.ServiceProvider;

/**
 * Ranking using single internal metric
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = Rank.class)
public class SORank<E extends Instance, C extends Cluster<E>> implements Rank<E, C> {

    private static final String NAME = "SO Rank";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Clustering<E, C>[] sort(Clustering<E, C>[] clusterings, List<ClusterEvaluation<E, C>> objectives) {
        if (objectives.size() != 1) {
            throw new RuntimeException("Please provide single evaluation metric. " + objectives.size() + " was given");
        }

        Arrays.parallelSort(clusterings, new ClusteringComparator(objectives.get(0)));

        return clusterings;
    }

    @Override
    public boolean isMultiObjective() {
        return false;
    }

}
