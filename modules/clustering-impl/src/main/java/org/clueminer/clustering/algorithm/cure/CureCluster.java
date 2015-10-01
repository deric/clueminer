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
    public ArrayList pointsInCluster = new ArrayList();
    public double distanceFromClosest = 0;
    public CureCluster<E> closestCluster;
    public ArrayList<Integer> closestClusterRep = new ArrayList();

    public double computeDistanceFromCluster(CureCluster<E> cluster, Distance dm) {
        double minDistance = 1000000;
        double distance;
        for (E p1 : rep) {
            for (E p2 : cluster.rep) {
                distance = dm.measure(p1, p2);
                if (minDistance > distance) {
                    minDistance = distance;
                }
            }
        }
        return minDistance;
    }

    public int getClusterSize() {
        return pointsInCluster.size();
    }

    public ArrayList getPointsInCluster() {
        return pointsInCluster;
    }

}
