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
package org.clueminer.chameleon.mo;

import org.clueminer.chameleon.Chameleon;
import org.clueminer.chameleon.GraphCluster;
import org.clueminer.dataset.api.Instance;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Multi-objective merger (heap sorting). Uses {@link FrontHeapQueue}
 *
 * Uses several optimizations:
 * - items to be added are compared to last front item first
 * - when both objectives are equal to zero, item is not added at all
 *
 * @author deric
 */
@ServiceProvider(service = Merger.class)
public class PairMergerMOH<E extends Instance, C extends GraphCluster<E>, P extends MoPair<E, C>> extends PairMergerMO<E, C, P> implements Merger<E> {

    public static final String NAME = "MOM-HS";

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
        P pair;
        int skip = 0;
        //generate all pairs
        for (int i = 0; i < clusters.size(); i++) {
            c1 = (C) clusters.get(i);
            for (int j = 0; j < i; j++) {
                c2 = (C) clusters.get(j);
                pair = (P) createPair(c1, c2, pref);
                //eliminate pair that won't be merged (doesn't share NN)
                if (pair.getObjective(0) != 0.0 && pair.getObjective(1) != 0.0) {
                    queue.add(pair);
                } else {
                    //else: don't even add such point to queue
                    skip++;
                }
            }
        }
        int debug = pref.getInt("debug", 0);
        if (debug > 0) {
            System.out.println("skipping " + skip + " pairs during merging initialization");
        }
    }

    @Override
    protected void initQueue(Props pref) {
        queue = new FrontHeapQueue<>(pref.getInt(Chameleon.NUM_FRONTS, 5), blacklist, objectives, pref);
    }

}
