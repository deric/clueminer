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
package org.clueminer.chameleon.mo;

import org.clueminer.chameleon.GraphCluster;
import org.clueminer.dataset.api.Instance;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Merger without filtering during initialization phase.
 *
 * @author deric
 */
@ServiceProvider(service = Merger.class)
public class PairMergerMOHff<E extends Instance, C extends GraphCluster<E>, P extends MoPair<E, C>> extends PairMergerMOH<E, C, P> implements Merger<E> {

    public static final String NAME = "MOM-HS-no-filter";

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Generate all possible (unique) combinations of pairs
     * - complexity O(n^2) = O(n * (n-1) / 2)
     *
     * @param queue
     * @param pref
     */
    @Override
    protected void fillQueue(AbstractQueue<E, C, P> queue, Props pref) {
        C c1, c2;
        //generate all pairs
        for (int i = 0; i < clusters.size(); i++) {
            c1 = (C) clusters.get(i);
            for (int j = 0; j < i; j++) {
                c2 = (C) clusters.get(j);
                queue.add((P) createPair(c1, c2, pref));
            }
        }
    }
}
