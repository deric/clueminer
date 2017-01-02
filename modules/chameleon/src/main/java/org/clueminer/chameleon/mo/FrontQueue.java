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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * Simple queue for getting items from Pareto front
 *
 * @author deric
 */
public class FrontQueue<E extends Instance, C extends Cluster<E>, P extends MoPair<E, C>> implements Iterator<P> {

    private LinkedList<LinkedList<P>> fronts;
    private NSGASort<E, C, P> sorter;
    private List<MergeEvaluation<E>> objectives;
    private ArrayList<P> pairs;
    private Props props;

    private int currFront = 0;

    private int currItem = 0;

    public FrontQueue(ArrayList<P> pairs, List<MergeEvaluation<E>> objectives, Props pref) {
        sorter = new NSGASort<>();
        this.objectives = objectives;
        this.pairs = pairs;
        this.props = pref;
        this.fronts = sorter.sort(pairs, objectives, pref);
    }

    public FrontQueue(LinkedList<LinkedList<P>> fronts) {
        this.fronts = fronts;
    }

    /**
     * Removes the first item from the first front, if any
     *
     * @return first item or null
     */
    public P poll() {
        P item = null;
        if (fronts.isEmpty()) {
            return null;
        }
        LinkedList<P> front;
        while (!fronts.isEmpty()) {
            front = fronts.get(0);
            if (front.isEmpty()) {
                fronts.remove(0);
            } else {
                item = front.removeFirst();
            }

            if (item != null) {
                return remove(item);
            }
        }

        return item;
    }

    P remove(P item) {
        if (pairs != null) {
            pairs.remove(item);
        }
        return item;
    }

    @Override
    public boolean hasNext() {
        if (fronts.isEmpty()) {
            return false;
        }
        int curr = 0;

        List<P> front = fronts.get(curr);
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
        int curr = 0;

        List<P> front = fronts.get(curr);
        while (curr < fronts.size() && front != null) {
            if (front.size() > 0) {
                return false;
            }
            front = fronts.get(curr++);
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
        int curr = 0;
        List<P> front = fronts.get(currFront);
        while (curr < fronts.size() && front != null) {
            front = fronts.get(curr++);
            size += front.size();
        }
        return size;
    }

    @Override
    public P next() {
        P item = null;
        List<P> front = fronts.get(currFront);
        while (currFront < fronts.size() && front != null) {
            if (currItem < front.size()) {
                return front.get(currItem++);
            }
            front = fronts.get(++currFront);
            currItem = 0;
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
        this.fronts = sorter.sort(pairs, objectives, props);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FrontQueue [");
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

}
