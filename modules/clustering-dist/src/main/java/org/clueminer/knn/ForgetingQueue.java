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

import it.unimi.dsi.fastutil.objects.Object2DoubleLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleSortedMap;
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
    private final Object2DoubleSortedMap<Instance> queue;
    private final DistanceMeasure dm;
    private final Instance target;
    private final int k;

    public ForgetingQueue(int k, DistanceMeasure dm, Instance target) {
        this.dm = dm;
        this.queue = new Object2DoubleLinkedOpenHashMap<>(k);
        this.target = target;
        this.k = k;
    }

    public void check(Instance inst) {
        double dist = dm.measure(target, inst);
        while (queue.size() < k) {
            queue.put(inst, dist);
            return;
        }

        Instance last = queue.lastKey();
        double lastDist = queue.get(last);
        //compare against worst element
        if (dm.compare(dist, lastDist)) {
            queue.remove(last);
            queue.put(inst, dist);
        }
    }

    public Instance[] getResult() {
        return queue.keySet().toArray(new Instance[0]);
    }

    public String dumpInst() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Map.Entry<Instance, Double> entry : queue.entrySet()) {
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
        for (Map.Entry<Instance, Double> entry : queue.entrySet()) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(entry.getValue());
            i++;
        }
        return sb.toString();
    }

}
