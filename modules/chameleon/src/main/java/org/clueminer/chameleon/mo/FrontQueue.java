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

import java.util.List;

/**
 * Simple queue for getting items from Pareto front
 *
 * @author deric
 */
public class FrontQueue<Q> {

    private final List<List<Q>> fronts;

    private int currFront = 0;

    private int currItem = 0;

    public FrontQueue(List<List<Q>> fronts) {
        this.fronts = fronts;
    }

    public Q poll() {
        Q item = null;
        List<Q> front = fronts.get(currFront);
        while (currFront < fronts.size() && front != null) {
            if (currItem < front.size()) {
                return front.get(currItem++);
            }
            front = fronts.get(++currFront);
            currItem = 0;
        }

        return item;
    }

    public boolean isEmpty() {
        if (fronts.isEmpty()) {
            return true;
        }
        int curr = 0;

        List<Q> front = fronts.get(curr);
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
        List<Q> front = fronts.get(currFront);
        while (curr < fronts.size() && front != null) {
            front = fronts.get(curr++);
            size += front.size();
        }
        return size;
    }

}
