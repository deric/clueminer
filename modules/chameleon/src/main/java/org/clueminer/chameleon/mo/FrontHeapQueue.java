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
import org.clueminer.utils.Duple;
import org.clueminer.utils.Props;

/**
 * Multi-objective sorting that preserves first k fronts. The order is
 * approximated because we don't compare an item with all front members.
 *
 * @author deric
 */
public class FrontHeapQueue<E extends Instance, C extends Cluster<E>, P extends MoPair<E, C>> implements Iterable<P> {

    private Heap<P>[] fronts;
    private final DominanceComparator<E, C, P> comparator;

    private int lastFront = 0;
    //maximum number of fronts
    private int maxFront;
    ArrayList<P> buffer;
    final HashSet<Integer> blacklist;
    private MoPairComparator itemCmp;
    private int frontsRemoved = 0;

    /**
     *
     * @param max        number of fronts kept
     * @param blacklist
     * @param objectives
     * @param pref
     */
    public FrontHeapQueue(int max, HashSet<Integer> blacklist, List<MergeEvaluation<E>> objectives, Props pref) {
        this.comparator = new DominanceComparator(objectives);
        this.buffer = new ArrayList<>();
        //maximum number of fronts
        maxFront = max;
        this.blacklist = blacklist;
        this.fronts = new Heap[maxFront];
        this.itemCmp = new MoPairComparator();
    }

    /**
     * Removes the first item from the first front, if any
     *
     * @return first item or null
     */
    public P poll() {
        if (isEmpty() && buffer.isEmpty()) {
            return null;
        }
        P item;
        int curr = 0;
        Heap<P> front;

        do {
            front = getFront(curr++);
            if (front.size() > 0) {
                item = front.pop();
                if (front.isEmpty()) {
                    frontsRemoved++;
                    removeFront(curr - 1);
                }
                if (frontsRemoved > 1) {
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
        return buffer.size() > 0;
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
        return size + buffer.size();
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
            int currFront = 0;
            int offset = index;
            P item = null;
            int paretoSize = 0;
            Heap<P> front;
            do {
                front = fronts[currFront++];
                if (front != null) {
                    if (offset >= front.size()) {
                        offset -= front.size();
                    } else {
                        item = front.get(offset);
                        index++;
                        return item;
                    }
                    paretoSize += front.size();
                }

            } while (currFront < fronts.length && front != null);
            if (item == null) {
                int idx = index - paretoSize;
                item = buffer.get(idx);
            }
            index++;
            return item;
        }
    }

    public void add(P pair) {
        //try to insert into first front
        add(pair, 0, buffer);
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
        Heap<P> front = getFront(curr);
        if (front.isEmpty()) {
            front.add(pair);
            return;
        }

        flagDominate = comparator.compare(pair, front.peek());
        switch (flagDominate) {
            case -1:
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
                }   //shift all fronts one down
                Heap<P>[] tmp = new Heap[maxFront];
                if (curr > 0) {
                    //copy first fronts up to curr
                    System.arraycopy(fronts, 0, tmp, 0, curr);
                }   //insert new front
                tmp[curr] = ff;
                //copy rest of fronts except last one
                System.arraycopy(fronts, curr, tmp, curr + 1, maxFront - 1 - curr);
                fronts = tmp;
                break;
            case 1:
                if (curr < maxFront) {
                    add(pair, ++curr, buffer);
                } else {
                    //last resort - save the item for later
                    buffer.add(pair);
                }
                break;
            case 0:
                //we can't decide which one dominates, item belongs to this front
                front.add(pair);
                break;
            default:
                break;
        }
    }

    public void addAll(Collection<P> coll) {
        for (P item : coll) {
            add(item);
        }
    }

    void rebuildQueue() {
        if (buffer.isEmpty() || blacklist.isEmpty()) {
            return;
        }
        ArrayList<P> tmp = new ArrayList<>(buffer.size());
        Iterator<P> iter;
        P elem;
        for (Heap<P> front : fronts) {
            if (front != null && !front.isEmpty()) {
                iter = front.iterator();
                while (iter.hasNext()) {
                    elem = iter.next();
                    buffer.add(elem);
                }
                front.clear();
            }
        }
        for (P item : buffer) {
            if (blacklist.contains(item.A.getClusterId()) || blacklist.contains(item.B.getClusterId())) {
                //skip the item
            } else {
                //try inserting into pareto front
                add(item, 0, tmp);
            }
        }
        //System.out.println("reduced pairs from " + pairs.size() + " to " + tmp.size());
        buffer = tmp;
    }

    /**
     * Remove blacklisted item from all current fronts
     *
     * @param item
     * @return
     */
    public int filterOut() {
        int removed = 0;
        //System.out.println("merging " + item.A.getClusterId() + ", " + item.B.getClusterId());
        //blacklist.add(item.A.getClusterId());
        //blacklist.add(item.B.getClusterId());
        //System.out.println("blacklist: " + blacklist);

        Heap<P> front;
        Duple<Integer, P> curr;
        for (int i = 0; i < fronts.length; i++) {
            front = fronts[i];
            if (front != null) {
                Iterator<Duple<Integer, P>> iter = front.indexValue();
                while (iter.hasNext()) {
                    curr = iter.next();
                    if (blacklist.contains(curr.y.A.getClusterId()) || blacklist.contains(curr.y.B.getClusterId())) {
                        //remove item - we have to use interal heap index
                        //System.out.println("removing [" + curr.y.A.getClusterId() + ", " + curr.y.B.getClusterId() + "]");
                        front.remove(curr.x);
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

    /**
     * Remove front with given index
     *
     * @param curr
     */
    private void removeFront(int curr) {
        //shift all fronts one up (remove first one)
        Heap<P>[] tmp = new Heap[maxFront];
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
    protected Heap<P> getFront(int i) {
        return getFront(fronts, i);
    }

    private Heap<P> getFront(Heap<P>[] paretoF, int i) {
        if (i > lastFront) {
            lastFront = i;
        }
        if (paretoF[i] == null) {
            paretoF[i] = new Heap<>(itemCmp);
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
