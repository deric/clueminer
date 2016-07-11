/*
 * Copyright (C) 2011-2016 clueminer.org
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.dataset.api.Instance;
import org.clueminer.sort.Heap;
import org.clueminer.utils.Props;

/**
 * Test pair if dominate all other pairs on the front.
 *
 * @author deric
 */
public class FrontHeapQueueDA<E extends Instance, C extends Cluster<E>, P extends MoPair<E, C>> extends FrontHeapQueue<E, C, P> implements Iterable<P> {

    public FrontHeapQueueDA(int max, HashSet<Integer> blacklist, List<MergeEvaluation<E>> objectives, Props pref) {
        super(max, blacklist, objectives, pref);
    }

    /**
     * Try inserting item into the Pareto front
     *
     * @param pair
     * @param curr
     * @param buffer list of items that does not fit to the front
     */
    @Override
    public boolean add(P pair, int curr, ArrayList<P> buffer) {
        int flagDominate;
        if (curr >= maxFront) {
            buffer.add(pair);
            return true;
        }
        Heap<P> front = getFront(curr);
        if (front.isEmpty()) {
            front.add(pair);
            return true;
        }
        boolean dominateAll = true;
        for (P item : front) {
            flagDominate = comparator.compare(pair, item);
            if (flagDominate == 1) {
                if (curr < maxFront) {
                    return add(pair, ++curr, buffer);
                } else {
                    //last resort - save the item for later
                    buffer.add(pair);
                    return false;
                }
            } else if (flagDominate == 0) {
                dominateAll = false;
            }
        }

        if (!dominateAll) {
            //there's at least on item that is non-dominating
            //we can't decide which one dominates, item belongs to this front
            front.add(pair);
            return true;
        }

        //item dominates whole front
        Heap<P> ff = new Heap<>(itemCmp);
        ff.add(pair);
        //item dominates all known fronts
        if (fronts[maxFront - 1] != null) {
            //move last front to "do it later" list
            Iterator<P> iter = fronts[maxFront - 1].iterator();
            while (iter.hasNext()) {
                buffer.add(iter.next());
            }
            //free memory
            fronts[maxFront - 1] = null;
        }
        //shift all fronts one down
        Heap<P>[] tmp = new Heap[maxFront];
        if (curr > 0) {
            //copy first fronts up to curr
            System.arraycopy(fronts, 0, tmp, 0, curr);
        }
        //insert new front
        tmp[curr] = ff;
        //copy rest of fronts except last one
        System.arraycopy(fronts, curr, tmp, curr + 1, maxFront - 1 - curr);
        fronts = tmp;

        return true;

    }

}
