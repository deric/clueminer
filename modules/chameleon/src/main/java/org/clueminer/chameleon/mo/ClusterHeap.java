/*
 * Copyright (C) 2011-2018 clueminer.org
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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.dataset.api.Instance;
import org.clueminer.sort.Heap;

/**
 *
 * @author deric
 */
public class ClusterHeap<E extends Instance, C extends Cluster<E>, P extends MoPair<E, C>> extends Heap<P> {

    private final Map<Integer, Integer> clust2heap;

    public ClusterHeap(Comparator<P> comparator) {
        super(comparator);
        clust2heap = new HashMap<>();
    }

    @Override
    public void add(final P element) {
        super.add(element);
        clust2heap.put(element.A.getClusterId(), size() - 1);
        clust2heap.put(element.B.getClusterId(), size() - 1);
    }

    public boolean removeCluster(int clusterId) {
        if (clust2heap.containsKey(clusterId)) {
            int index = clust2heap.get(clusterId);
            super.remove(index);
            clust2heap.remove(clusterId);
            return true;
        }
        return false;
    }

}
