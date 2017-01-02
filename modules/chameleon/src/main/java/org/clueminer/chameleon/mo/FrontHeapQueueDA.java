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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.dataset.api.Instance;
import org.clueminer.sort.Heap;
import org.clueminer.utils.Duple;
import org.clueminer.utils.Props;

/**
 * Test pair if dominate all other pairs on the front.
 *
 * @TODO method is currently suboptimal: items moving causes strange behavior
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
        HashSet<Integer> toRemove = new HashSet<>(5);
        Iterator<Duple<Integer, P>> it = front.indexValue();
        Duple<Integer, P> dup;
        P item;
        while (it.hasNext()) {
            dup = it.next();
            item = dup.y;
            //System.out.println("pair: " + pair);
            //System.out.println("item: " + item);
            flagDominate = comparator.compare(pair, item);
            //System.out.println(flagDominate);
            switch (flagDominate) {
                case 1:
                    if (curr < maxFront) {
                        return add(pair, ++curr, buffer);
                    } else {
                        //last resort - save the item for later
                        buffer.add(pair);
                        return false;
                    }
                case 0:
                    dominateAll = false;
                    break;
                case -1:
                    //remove dominated items (move to next front)
                    //System.out.println("curr: " + pair);
                    //System.out.println("item: " + item);
                    toRemove.add(dup.x); //mark for removal
                    break;
                default:
                    break;
            }
        }

        if (dominateAll) {
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

        if (!toRemove.isEmpty()) {
            /* System.out.println("to RM " + toRemove.toString());
             * System.out.println("adding " + pair);
             * System.out.println("before"); */
            //front.print();

            front.add(pair);
            //curr is better than some solution(s) on the front, but not all of them
            for (int x : toRemove) {
                item = front.remove(x);
                // we're changing heap while iterating over
                if (item != null) {
                    flagDominate = comparator.compare(pair, item);
                    //make sure we have correct item
                    if (flagDominate == -1) {
                        add(item, (curr + 1), buffer);
                    }
                }
            }
            //System.out.println("after");
            //front.print();
            return true;
        }

        if (!dominateAll) {
            //we can't decide which one dominates, item belongs to this front
            front.add(pair);
            return true;
        }
        throw new RuntimeException("should not happen");
        //return false;
    }

}
