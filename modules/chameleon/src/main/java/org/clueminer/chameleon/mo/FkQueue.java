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
package org.clueminer.chameleon.mo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * Multi-objective sorting that preserves first k fronts. The order is
 * approximated because we don't compare an item with all front members.
 *
 * @author deric
 */
public class FkQueue<E extends Instance, C extends Cluster<E>, P extends MoPair<E, C>> implements Iterable<P> {

    private LinkedList<P>[] fronts;
    private final DominanceComparator<E, C, P> comparator;

    private int lastFront = 0;
    //maximum number of fronts
    private int maxFront;
    private ArrayList<P> pairs;
    private HashSet<Integer> blacklist;

    /**
     *
     * @param max number of fronts kept
     * @param blacklist
     * @param objectives
     * @param pref
     */
    public FkQueue(int max, HashSet<Integer> blacklist, List<MergeEvaluation<E>> objectives, Props pref) {
        this.comparator = new DominanceComparator(objectives);
        this.pairs = new ArrayList<>();
        //maximum number of fronts
        maxFront = max;
        this.blacklist = blacklist;
        this.fronts = new LinkedList[maxFront];
    }

    /**
     * Removes the first item from the first front, if any
     *
     * @return first item or null
     */
    public P poll() {
        if (isEmpty() && pairs.isEmpty()) {
            return null;
        }
        P item;
        int curr = 0;
        LinkedList<P> front;

        filterOut();
        do {
            front = getFront(curr++);
            if (front.size() > 0) {
                item = front.pop();
                if (emptyFronts() > 2) {
                    rebuildQueue();
                    //System.out.println("after: " + toString());
                } else {
                    if (front.isEmpty()) {
                        removeFront(curr - 1);
                    }
                }

                return item;
            }
        } while (curr < fronts.length);

        //rebuildQueue();
        return null;
    }

    private void removeFront(int curr) {
        //shift all fronts one up (remove first one)
        LinkedList<P>[] tmp = new LinkedList[maxFront];
        if (curr == 0) {
            //copy first fronts up to curr
            System.arraycopy(fronts, 1, tmp, 0, maxFront - 1);
        } else {            //first part
            System.arraycopy(fronts, 0, tmp, 0, curr);
            //skip curr
            //copy rest
            System.arraycopy(fronts, curr + 1, tmp, curr, maxFront - curr - 1);
        }
        //update front list
        fronts = tmp;
    }

    public int emptyFronts() {
        int empty = 0;
        for (LinkedList<P> front : fronts) {
            if (front == null || front.isEmpty()) {
                empty++;
            }
        }
        return empty;
    }

    /**
     * Remove item from current fronts
     *
     * @param item
     * @return
     */
    public int filterOut() {
        int removed = 0;
        //System.out.println("filtering " + item.A.getClusterId() + ", " + item.B.getClusterId());
        //blacklist.add(item.A.getClusterId());
        //blacklist.add(item.B.getClusterId());
        P curr;
        LinkedList<P> front;
        for (int i = 0; i < fronts.length; i++) {
            front = fronts[i];
            if (front != null) {
                for (int j = 0; j < front.size(); j++) {
                    curr = front.get(j);
                    if (blacklist.contains(curr.A.getClusterId()) || blacklist.contains(curr.B.getClusterId())) {
                        //remove item
                        front.remove(j);
                        removed++;
                    }
                    if (front.isEmpty()) {
                        removeFront(i);
                    }
                }
            }
        }
        return removed;
    }

    public boolean hasNext() {
        int curr = 0;

        LinkedList<P> front = fronts[curr];
        while (curr < fronts.length && front != null) {
            if (front.size() > 0) {
                return true;
            }
            front = fronts[curr++];
        }
        return pairs.size() > 0;
    }

    /**
     * While Pareto front is empty, the whole queue might not be empty -- there
     * might still be items located in pair store
     *
     * @return true when Pareto front is empty
     */
    public boolean isEmpty() {
        boolean empty = true;
        for (LinkedList<P> front : fronts) {
            empty = empty && (front == null || front.isEmpty());
            if (!empty) {
                return false;
            }
        }
        return true;
    }

    /**
     * Total number of items in all fronts
     *
     * @return
     */
    public int size() {
        int size = 0;
        if (fronts.length > 0) {
            //reduce(0) { front.size }
            for (LinkedList<P> front : fronts) {
                if (front != null) {
                    size += front.size();
                }
            }
        }
        return size + pairs.size();
    }

    @Override
    public Iterator<P> iterator() {
        return new FrontIterator();
    }

    class FrontIterator implements Iterator<P> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from instance using the iterator.");

        }

        @Override
        public P next() {
            index++;
            int currFront = 0;
            int offset = index;
            P item = null;
            LinkedList<P> front;
            do {
                front = fronts[currFront++];
                if (front != null) {
                    if (offset >= front.size()) {
                        offset -= front.size();
                    } else {
                        return front.get(offset);
                    }
                }

            } while (currFront < fronts.length && front != null);

            return item;
        }
    }

    public void add(P pair) {
        //try to insert into first front
        add(pair, 0, pairs);
    }

    /**
     * Try inserting item into the Pareto front
     *
     * @param pair
     * @param curr
     * @param buffer list of items that does not fit to the front
     */
    public void add(P pair, int curr, ArrayList<P> buffer) {
        int flagDominate;
        if (curr >= maxFront) {
            buffer.add(pair);
            return;
        }
        LinkedList<P> front = getFront(curr);
        if (front.isEmpty()) {
            front.add(pair);
            return;
        }

        //test if item dominate all front members
        for (P item : front) {
            flagDominate = comparator.compare(pair, item);

            if (flagDominate == 1) {
                if (curr < maxFront) {
                    add(pair, ++curr, buffer);
                    return;
                } else {
                    //last resort - save the item for later
                    buffer.add(pair);
                    return;
                }
            } else if (flagDominate == 0) {
                //we can't decide which one dominates, item belongs to this front
                front.add(pair);
                return;
            }
        }

        //item dominates whole front
        LinkedList<P> ff = new LinkedList<>();
        ff.add(pair);
        //item dominates all known fronts

        if (fronts[maxFront - 1] != null) {
            //move last front to "do it later" list
            for (P it : fronts[maxFront - 1]) {
                buffer.add(it);
            }
            //free memory
            fronts[maxFront - 1] = null;
        }
        //shift all fronts one down
        LinkedList<P>[] tmp = new LinkedList[maxFront];
        if (curr > 0) {
            //copy first fronts up to curr
            System.arraycopy(fronts, 0, tmp, 0, curr);
        }
        //insert new front
        tmp[curr] = ff;
        //copy rest of fronts except last one
        System.arraycopy(fronts, curr, tmp, curr + 1, maxFront - 1 - curr);
        fronts = tmp;
    }

    public void addAll(Collection<P> coll) {
        for (P item : coll) {
            add(item);
        }
    }

    private void rebuildQueue() {
        if (pairs.isEmpty()) {
            return;
        }
        ArrayList<P> tmp = new ArrayList<>(pairs.size());
        for (LinkedList<P> front : fronts) {
            if (front != null && !front.isEmpty()) {
                for (P elem : front) {
                    pairs.add(elem);
                }
                front.clear();
            }
        }
        for (P item : pairs) {
            if (blacklist.contains(item.A.getClusterId()) || blacklist.contains(item.B.getClusterId())) {
                //skip the item
            } else {
                //try inserting into pareto front
                add(item, 0, tmp);
            }
        }
        //we can safely clear blacklist
        //blacklist = new HashSet<>(pairs.size());
        pairs = tmp;
    }

    /**
     * Ensure that front with given ID exists
     *
     * @param i
     * @return front with required rank (start from 0 up to (n+1))
     */
    protected LinkedList<P> getFront(int i) {
        return getFront(fronts, i);
    }

    private LinkedList<P> getFront(LinkedList<P>[] paretoF, int i) {
        if (i > lastFront) {
            lastFront = i;
        }
        if (paretoF[i] == null) {
            paretoF[i] = new LinkedList<>();
        }

        return paretoF[i];
    }

    public int numFronts() {
        if (fronts == null) {
            return 0;
        }
        return lastFront + 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < fronts.length; j++) {
            LinkedList<P> curr = getFront(j);
            sb.append("front ").append(j).append(" size: ").append(curr.size()).append("\n");
            Iterator<P> iter = curr.iterator();
            P item;
            while (iter.hasNext()) {
                item = iter.next();
                sb.append("  ").append(item).append("; ");
                sb.append("A:").append(item.A.getClusterId()).append(", ");
                sb.append("B:").append(item.B.getClusterId()).append("\n");
            }

        }
        return sb.toString();
    }

    public String stats() {
        StringBuilder sb = new StringBuilder("FkQueue [");
        if (fronts != null) {
            for (int i = 0; i < fronts.length; i++) {
                if (i > 0) {
                    sb.append("\n");
                }
                sb.append("front ").append(i);
                if (fronts[i] != null) {
                    sb.append("[").append(fronts[i].size()).append("]").append(": ");
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
