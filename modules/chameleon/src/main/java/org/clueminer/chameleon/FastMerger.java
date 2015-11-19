/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.chameleon;

import org.clueminer.dataset.api.Instance;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.PairValue;
import org.clueminer.utils.Props;

/**
 * An experimental merger without necessity of merging O(n^2) items
 *
 * @author deric
 * @param <E>
 */
public class FastMerger<E extends Instance> extends PairMerger<E> implements Merger<E> {

    public static final String name = "fast merger";

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void buildQueue(int numClusters, Props pref) {
        int capacity = numClusters * numClusters;
        pq = initQueue(capacity);
        double sim;
        GraphCluster<E> a, b;
        for (int i = 0; i < numClusters; i++) {
            a = clusters.get(i);
            /**
             * TODO: go through nodes, add unique clusters
             */
            for (int j = 0; j < i; j++) {
                b = clusters.get(j);
                sim = evaluation.score(a, b, pref);
                pq.add(new PairValue<>(a, b, sim));
            }
        }
    }
}
