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
package org.clueminer.clustering.algorithm.cure;

import java.util.Comparator;
import org.clueminer.dataset.api.Instance;

/**
 * Defines a comparator which helps the MinHeap (Implemented using Priority
 * Queue) to compare two clusters and store accordingly in the heap.
 *
 * Two clusters are compared based on the distance from their closest Cluster.
 * The cluster pair which has the lowest such distance is stored at the root of
 * the min heap.
 *
 * @author deric
 * @param <E>
 */
public class CureComparator<E extends Instance> implements Comparator<CureCluster<E>> {

    @Override
    public int compare(CureCluster<E> o1, CureCluster<E> o2) {
        if (o1.distClosest < o2.distClosest) {
            return -1;
        } else if (o1.distClosest == o2.distClosest) {
            return 0;
        } else {
            return 1;
        }
    }

}
