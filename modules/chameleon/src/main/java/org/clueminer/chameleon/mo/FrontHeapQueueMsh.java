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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.dataset.api.Instance;
import org.clueminer.sort.Heap;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class FrontHeapQueueMsh<E extends Instance, C extends Cluster<E>, P extends MoPair<E, C>> implements Iterable<P> {

    private ClusterHeap<E, C, P>[] fronts;
    private final DominanceComparator<E, C, P> comparator;

    private int lastFront = 0;
    //maximum number of fronts
    private int maxFront;
    ArrayList<P> pairs;
    final HashSet<Integer> blacklist;
    private MoPairComparator itemCmp;
    private int frontsRemoved = 0;

    /**
     *
     * @param max number of fronts kept
     * @param blacklist
     * @param objectives
     * @param pref
     */
    public FrontHeapQueueMsh(int max, HashSet<Integer> blacklist, List<MergeEvaluation<E>> objectives, Props pref) {
        this.comparator = new DominanceComparator(objectives);
        this.pairs = new ArrayList<>();
        //maximum number of fronts
        maxFront = max;
        this.blacklist = blacklist;
        this.fronts = new ClusterHeap[maxFront];
        this.itemCmp = new MoPairComparator();
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
        ClusterHeap<E, C, P> front;

        do {
            front = getFront(curr++);
            if (front.size() > 0) {
                item = front.pop();
                if (front.isEmpty()) {
                    frontsRemoved++;
                    removeFront(curr - 1);
                }
                if (frontsRemoved > (maxFront / 2)) {
                    rebuildQueue();
                    frontsRemoved = 0;
                }
                return item;
            }
        } while (curr < fronts.length);

        rebuildQueue();
        return poll();
    }

    public boolean hasNext() {
        int curr = 0;

        Heap<P> front = fronts[curr];
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
        for (Heap<P> front : fronts) {
            empty = empty && (front == null || front.isEmpty());
            if (!empty) {
                return false;
            }
        }
        return true;
    }

    public int emptyFronts() {
        int empty = 0;
        for (Heap<P> front : fronts) {
            if (front == null || front.isEmpty()) {
                empty++;
            }
        }
        return empty;
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
            for (Heap<P> front : fronts) {
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
            Heap<P> front;
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
        if (!insertIntoFront(pair, 0)) {
            //if item doesn't fit into pareto front, save it for later
            pairs.add(pair);
        }
    }

    /**
     * Try inserting item into the Pareto front
     *
     * @param pair
     * @param curr
     * @param buffer list of items that does not fit to the front
     */
    public boolean insertIntoFront(P pair, int curr) {
        int flagDominate;
        if (curr >= maxFront) {
            return false;
        }
        Heap<P> front = getFront(curr);
        if (front.isEmpty()) {
            front.add(pair);
            return true;
        }
        for (P item : front) {
            flagDominate = comparator.compare(pair, item);
            if (flagDominate == 1) {
                if (curr < maxFront) {
                    return insertIntoFront(pair, ++curr);
                } else {
                    //last resort - save the item for later
                    return false;
                }
            } else if (flagDominate == 0) {
                //we can't decide which one dominates, item belongs to this front
                front.add(pair);
                return true;
            }
        }

        //item dominates whole front
        ClusterHeap<E, C, P> ff = new ClusterHeap<>(itemCmp);
        ff.add(pair);
        //item dominates all known fronts
        if (fronts[maxFront - 1] != null) {
            //move last front to "do it later" list
            Iterator<P> iter = fronts[maxFront - 1].iterator();
            while (iter.hasNext()) {
                pairs.add(iter.next());
            }
            //free memory
            fronts[maxFront - 1] = null;
        }
        //shift all fronts one down
        ClusterHeap<E, C, P>[] tmp = new ClusterHeap[maxFront];
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

    public void addAll(Collection<P> coll) {
        for (P item : coll) {
            add(item);
        }
    }

    void rebuildQueue() {
        if (pairs.isEmpty() || blacklist.isEmpty()) {
            return;
        }
        Iterator<P> iter;
        P elem;
        for (Heap<P> front : fronts) {
            if (front != null && !front.isEmpty()) {
                iter = front.iterator();
                while (iter.hasNext()) {
                    elem = iter.next();
                    pairs.add(elem);
                }
                front.clear();
            }
        }

        P item;
        int originalSize = (pairs.size() - 1);
        int size;
        for (int i = originalSize; i >= 0; i--) {
            //index of last item
            size = pairs.size() - 1;
            item = pairs.get(i);

            if (blacklist.contains(item.A.getClusterId()) || blacklist.contains(item.B.getClusterId())) {
                //remove the item
                removePair(pairs, item, i, size);
            } else if (insertIntoFront(item, 0)) {
                //item is in the front, we can remove it
                removePair(pairs, item, i, size);
            }
            //dumpPairs();
        }
        //minimize storage requirements (will reallocate array)
        //pairs.trimToSize();
    }

    private void removePair(ArrayList<P> pairs, P item, int i, int size) {
        P tmp;
        if (i == size) {
            //last item can be safely removed (without reallocation of an array)
            pairs.remove(i);
        } else {
            //swap current item with last one
            tmp = item;
            pairs.set(i, pairs.get(size));
            pairs.set(size, tmp);
            //let GC do its work
            pairs.remove(size);
        }
    }

    private void dumpPairs() {
        P pair;
        for (int j = 0; j < pairs.size(); j++) {
            pair = pairs.get(j);
            if (j > 0) {
                System.out.print(", ");
            }
            System.out.print(j + ": [" + pair.A.getClusterId() + "," + pair.B.getClusterId() + "]");
        }
        System.out.println("");
    }

    /**
     * Remove blacklisted item from all current fronts
     *
     * @param item
     * @return
     */
    public int filterOut(P pair) {
        int removed = 0;
        //System.out.println("merging " + pair.A.getClusterId() + ", " + pair.B.getClusterId());
        //blacklist.insertIntoFront(item.A.getClusterId());
        //blacklist.insertIntoFront(item.B.getClusterId());
        //System.out.println("blacklist: " + blacklist);

        ClusterHeap<E, C, P> front;
        for (int i = 0; i < fronts.length; i++) {
            front = fronts[i];
            if (front != null) {
                if (front.removeCluster(pair.A.getClusterId())) {
                    removed++;
                }
                if (!front.isEmpty()) {
                    if (front.removeCluster(pair.B.getClusterId())) {
                        removed++;
                    }
                }

                if (front.isEmpty()) {
                    removeFront(i);
                }
            }
        }
        return removed;
    }

    /**
     * Remove front with given index
     *
     * @param curr
     */
    private void removeFront(int curr) {
        //shift all fronts one up (remove first one)
        ClusterHeap<E, C, P>[] tmp = new ClusterHeap[maxFront];
        if (curr == 0) {
            //copy first fronts up to curr
            System.arraycopy(fronts, 1, tmp, 0, maxFront - 1);
        } else {
            //first part
            System.arraycopy(fronts, 0, tmp, 0, curr);
            //skip curr
            //copy rest
            System.arraycopy(fronts, curr + 1, tmp, curr, maxFront - curr - 1);
        }
        //update front list
        fronts = tmp;
    }

    /**
     * Ensure that front with given ID exists
     *
     * @param i
     * @return front with required rank (start from 0 up to (n+1))
     */
    protected ClusterHeap<E, C, P> getFront(int i) {
        return getFront(fronts, i);
    }

    private ClusterHeap<E, C, P> getFront(ClusterHeap<E, C, P>[] paretoF, int i) {
        if (i > lastFront) {
            lastFront = i;
        }
        if (paretoF[i] == null) {
            paretoF[i] = new ClusterHeap<>(itemCmp);
        }

        return paretoF[i];
    }

    class MoPairComparator implements Comparator<P> {

        @Override
        public int compare(P o1, P o2) {
            return o1.compareTo(o2);
        }

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
            Heap<P> curr = getFront(j);
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
