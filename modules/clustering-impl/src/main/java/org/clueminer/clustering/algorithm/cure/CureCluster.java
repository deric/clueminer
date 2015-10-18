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
package org.clueminer.clustering.algorithm.cure;

import java.util.ArrayList;
import java.util.Arrays;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;

/**
 *
 * @author deric
 * @param <E>
 */
public class CureCluster<E extends Instance> extends BaseCluster<E> implements Cluster<E> {

    private static final long serialVersionUID = 1719219542910904245L;

    public CureCluster(int capacity) {
        super(capacity);
    }

    /**
     * Create cluster with default size
     */
    public CureCluster() {
        super(10);
    }

    /**
     * Set of representative points
     */
    public ArrayList<E> rep = new ArrayList();
    public double distClosest = 0;
    public CureCluster<E> closest;

    /**
     * Distance between clusters. Distance function doesn't have to be a metric.
     *
     * @param cluster
     * @param dm distance measure
     * @return
     */
    public double dist(CureCluster<E> cluster, Distance dm) {
        double minDistance = Double.POSITIVE_INFINITY;
        double distance;
        for (E p1 : rep) {
            for (E p2 : cluster.rep) {
                distance = dm.measure(p1, p2);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        return minDistance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CureCluster ").append("#").append(getClusterId());
        sb.append("(").append(size()).append(") ");
        sb.append("centroid:").append(Arrays.toString(getCentroid().arrayCopy()));
        sb.append(" rep: {").append(rep.size()).append("} ");
        sb.append(" [ ");
        E elem;
        for (int i = 0; i < this.rep.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            elem = this.rep.get(i);
            sb.append(elem.toString());
        }
        sb.append(" ]");
        return sb.toString();
    }

}
