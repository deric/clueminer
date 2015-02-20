/*
 * Copyright (C) 2015 clueminer.org
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
package org.clueminer.knn;

import it.unimi.dsi.fastutil.objects.AbstractObject2ObjectMap;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceMeasure;

/**
 *
 * @author Tomas Barton
 *
 */
public class ForgetingQueue {

    /**
     * queue can't be indexed by distances because it would not necessarily be
     * an unique key
     */
    //private final Object2DoubleSortedMap<Instance> queue;
    //sorted map would be nice, but we need to sort by value
    private final List<Map.Entry<Instance, Double>> queue;
    private final DistanceMeasure dm;
    private final Instance target;
    private final int k;
    private final DmInstComparator comparator;

    public ForgetingQueue(int k, DistanceMeasure dm, Instance target) {
        this.queue = new LinkedList<>();
        this.dm = dm;
        this.comparator = new DmInstComparator(dm);
        //this.queue = new Object2DoubleAVLTreeMap<>(new DmInstComparator(dm));
        //queue = new TreeMap<>(new DmInstComparator<>(dm));
        //queue = new LinkedList<AbstractObject2ObjectMap.BasicEntry<Instance, Double>>();
        this.target = target;
        this.k = k;
    }

    public void check(Instance inst) {
        double dist = dm.measure(target, inst);
        while (queue.size() < k) {
            queue.add(new AbstractObject2ObjectMap.BasicEntry<>(inst, dist));
            sort();
            return;
        }

        Map.Entry<Instance, Double> last = queue.get(k - 1);
        //compare against worst element
        if (dm.compare(dist, last.getValue())) {
            queue.remove(last);
            queue.add(new AbstractObject2ObjectMap.BasicEntry<>(inst, dist));
            sort();
        }
    }

    private void sort() {
        Collections.sort(queue, comparator);
    }

    public Instance[] getResult() {
        Instance[] res = new Instance[queue.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = queue.get(i).getKey();
        }
        return res;
    }

    public String dumpInst() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Map.Entry<Instance, Double> entry : queue) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(entry.getKey().getIndex());
            i++;
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Map.Entry<Instance, Double> entry : queue) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(entry.getValue());
            i++;
        }
        return sb.toString();
    }

}
