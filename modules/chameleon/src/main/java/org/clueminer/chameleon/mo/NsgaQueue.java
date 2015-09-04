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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class NsgaQueue<E extends Instance, C extends Cluster<E>, P extends MoPair<C>> implements Iterator<P> {

    private ArrayList<Set<FndsMember<P>>> fronts;
    private List<MergeEvaluation<E>> objectives;
    private ArrayList<P> pairs;
    private Props params;
    private DominanceComparator<C, P> comparator;

    private int currFront = 0;

    private Iterator<FndsMember<P>> currIter;

    public NsgaQueue(ArrayList<P> pairs, List<MergeEvaluation<E>> objectives, Props pref) {
        this.objectives = objectives;
        this.pairs = pairs;
        this.params = pref;
        this.comparator = new DominanceComparator(objectives, params);
        System.out.println("got " + pairs.size() + " pairs");
        sort(pairs);
    }

    /**
     * Removes the first item from the first front, if any
     *
     * @return first item or null
     */
    public P poll() {
        FndsMember<P> item;
        if (fronts.isEmpty()) {
            return null;
        }
        //System.out.println(this.toString());
        int curr = 0;
        Set<FndsMember<P>> front = fronts.get(curr);
        Iterator<FndsMember<P>> iter = front.iterator();
        while (size() > 0) {
            if (iter.hasNext()) {
                item = iter.next();
                front.remove(item);
                return remove(item);
            }
            front = fronts.get(++curr);
            iter = front.iterator();
        }
        return null;
    }

    P remove(FndsMember<P> item) {
        if (pairs != null) {
            pairs.remove(item.getValue());
        }
        //make sure we update graph of dominancy
        //item.delete(fronts);
        return item.getValue();
    }

    @Override
    public boolean hasNext() {
        if (fronts.isEmpty()) {
            return false;
        }
        int curr = 0;

        Set<FndsMember<P>> front = fronts.get(curr);
        while (curr < fronts.size() && front != null) {
            if (front.size() > 0) {
                return true;
            }
            front = fronts.get(curr++);
        }
        return false;
    }

    public boolean isEmpty() {
        if (fronts.isEmpty()) {
            return true;
        }
        return size() == 0;
    }

    /**
     * Total number of items in all fronts
     *
     * @return
     */
    public int size() {
        int size = 0;
        if (fronts.size() > 0) {
            //reduce(0) { front.size }
            for (Set<FndsMember<P>> front : fronts) {
                size += front.size();
            }
        }
        return size;
    }

    @Override
    public P next() {
        P item = null;
        Set<FndsMember<P>> front = fronts.get(currFront);
        while (currFront < fronts.size() && front != null) {
            if (currIter == null) {
                currIter = front.iterator();
            }

            if (currIter.hasNext()) {
                return currIter.next().getValue();
            }
            front = fronts.get(++currFront);
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
        pairs.add(pair);
        //TODO: this is really inefficient, we have to resort all items for each insert
        sort(pairs);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("NsgaQueue [");
        if (fronts != null) {
            for (int i = 0; i < fronts.size(); i++) {
                sb.append("front ").append(i).append("[").append(fronts.get(i).size()).append("]").append(": ");
                /* for (P elem : fronts.get(i)) {                    sb.append(elem);
                 }*/
                sb.append("\n");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private void sort(ArrayList<P> pairs) {
        int n = pairs.size();
        int flagDominate;
        // front[i] contains the list of individuals belonging to the front i
        fronts = new ArrayList<>(n + 1);
        ArrayList<FndsMember<P>> members = new ArrayList<>();
        FndsMember<P> p, q;

        //wrap cluster pairs into sorting structure (a graph node)
        for (int i = 0; i < n; i++) {
            // Initialize the list of individuals
            p = new FndsMember<>(pairs.get(i));
            members.add(p);
        }

        for (int i = 0; i < (n - 1); i++) {
            // For all q individuals , calculate if p dominates q or vice versa
            p = members.get(i);
            for (int j = i + 1; j < n; j++) {
                q = members.get(j);
                flagDominate = comparator.compare(p.getValue(), q.getValue());
                if (flagDominate == -1) {
                    p.addIDominate(q);
                } else if (flagDominate == 1) {
                    q.addIDominate(p);
                }
            }
        }

        //find dominating items
        for (FndsMember<P> member : members) {
            if (member.rank() == 0) {
                getFront(0).add(member);
                member.setFront(0);
            }
        }

        //Obtain the rest of fronts
        int i = 0;
        Iterator<FndsMember<P>> it1, it2; // Iterators
        while (!getFront(i).isEmpty()) {
            i++;
            it1 = getFront(i - 1).iterator();
            while (it1.hasNext()) {
                it2 = it1.next().iterIDominate();
                while (it2.hasNext()) {
                    p = it2.next();
                    p.incDomCnt();
                    if (p.domDiff() == 0) {
                        getFront(i).add(p);
                        p.setFront(i);
                    }
                    //   System.out.println("q " + p.rank() + " rd: " + p.rankDiff() + " domDiff: " + p.domDiff() + " front " + p.frontAssign() + " i = " + i);
                }
            }
        }
        for (int j = 0; j < fronts.size(); j++) {
            Set<FndsMember<P>> curr = getFront(j);
            System.out.println("front " + j + " size: " + curr.size());
            for (FndsMember<P> item : curr) {
                System.out.println("  " + item.getFront() + " - " + item.getValue() + ", I dominate: " + item.getIDominateCnt() + " dominate me: " + item.getDominatesMeCnt() + " fa = " + item.frontAssign());
            }

        }
        System.out.println("total fronts: " + i);
    }

    /**
     * Ensure that front with given ID exists
     *
     * @param i
     * @return front with required rank (start from 0 up to (n+1))
     */
    private Set<FndsMember<P>> getFront(int i) {
        Set<FndsMember<P>> front = null;
        while (i >= fronts.size()) {
            front = new HashSet<>();
            fronts.add(front);
        }
        if (front == null) {
            front = fronts.get(i);
        }
        return front;
    }

    public int numFronts() {
        if (fronts == null) {
            return 0;
        }
        return fronts.size();
    }

}
