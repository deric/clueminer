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
 * Multi-objective sorting that preserves first k fronts
 *
 * @author deric
 */
public class FkQueue<E extends Instance, C extends Cluster<E>, P extends MoPair<C>> implements Iterator<P> {

    private LinkedList<P>[] fronts;
    private final Props params;
    private final DominanceComparator<C, P> comparator;

    private int currFront = 0;
    private int lastFront = 0;
    //maximum number of fronts
    private int maxFront;

    private Iterator<P> currIter;
    private ArrayList<P> pairs;
    private HashSet<Integer> blacklist;

    public FkQueue(HashSet<Integer> blacklist, List<MergeEvaluation<E>> objectives, Props pref) {
        this.params = pref;
        this.comparator = new DominanceComparator(objectives, params);
        this.pairs = new ArrayList<>();
        //maximum number of fronts
        maxFront = 5;
        this.blacklist = blacklist;
        this.fronts = new LinkedList[maxFront];
    }

    /**
     * Removes the first item from the first front, if any
     *
     * @return first item or null
     */
    public P poll() {
        if (isEmpty()) {
            //TODO: rebuild queue, if pairs not empty
            if (pairs.isEmpty()) {
                return null;
            }
            rebuildQueue();
        }
        //System.out.println(this.toString());
        int curr = 0;
        LinkedList<P> front = fronts[curr];
        while (curr < (fronts.length - 1)) {
            if (front.size() > 0) {
                return front.removeFirst();
            }
            front = fronts[++curr];
        }
        return null;
    }

    @Override
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

    public boolean isEmpty() {
        boolean empty = true;
        for (LinkedList<P> front : fronts) {
            empty = empty && front == null;
            if (!empty) {
                return false;
            }
        }
        return pairs.isEmpty();
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
    public P next() {
        P item = null;
        LinkedList<P> front = fronts[currFront];
        while (currFront < fronts.length && front != null) {
            if (currIter == null) {
                currIter = front.iterator();
            }

            if (currIter.hasNext()) {
                return currIter.next();
            }
            front = fronts[++currFront];
            currIter = front.iterator();
        }

        return item;
    }

    /**
     * Compatibility with java 7
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("not supported.");
    }

    public void add(P pair) {
        //try to insert into first front
        add(pair, 0);
    }

    public void add(P pair, int curr) {
        int flagDominate;
        LinkedList<P> front = getFront(curr);
        if (front.isEmpty()) {
            front.add(pair);
            return;
        }

        flagDominate = comparator.compare(pair, front.getFirst());
        if (flagDominate == -1) {
            //item dominates whole front
            LinkedList<P> ff = new LinkedList<>();
            ff.add(pair);
            //item dominates all known fronts

            if (fronts[maxFront - 1] != null) {
                //move last front to "do it later" list
                for (P item : fronts[maxFront - 1]) {
                    pairs.add(item);
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
            //copy rest of fronts exept last one
            System.arraycopy(fronts, curr, tmp, curr + 1, maxFront - 2 - curr);
            fronts = tmp;
        } else if (flagDominate == 1) {
            if (curr < maxFront) {
                add(pair, ++curr);
            } else {
                //last resort - save the item for later
                pairs.add(pair);
            }
        } else if (flagDominate == 0) {
            //we can't decide which one dominates, item belongs to this front
            front.addLast(pair);
        }
    }

    public void addAll(Collection<P> coll) {
        for (P item : coll) {
            add(item);
        }
    }

    private void rebuildQueue() {
        ArrayList<P> tmp = new ArrayList<>(pairs.size());
        for (P item : pairs) {
            if (blacklist.contains(item.A.getClusterId()) || blacklist.contains(item.B.getClusterId())) {
                //skip the item
            } else {
                //try inserting into pareto front
                add(item, 0);
            }
        }
        System.out.println("reduced pairs from " + pairs.size() + " to " + tmp.size());
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
            for (P item : curr) {
                sb.append("  ").append(item).append("\n");
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
