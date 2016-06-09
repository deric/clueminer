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
package org.clueminer.clustering.api;

import java.io.Serializable;

/**
 * A status object that represents the result of agglomerative merging of two
 * clusters. This class provides the information on which clusters were merged,
 * what the id of the remaining cluster is, and the similarity of the two
 * clusters at the point at which they were merged.
 *
 */
public class Merge implements Comparable, Serializable {

    private static final long serialVersionUID = 7366397676154738636L;
    private final int remainingCluster;
    private final int mergedCluster;
    private final double similarity;

    public Merge(int remainingCluster, int mergedCluster, double similarity) {
        this.remainingCluster = remainingCluster;
        this.mergedCluster = mergedCluster;
        this.similarity = similarity;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Merge) {
            Merge m = (Merge) o;
            return m.remainingCluster == remainingCluster
                    && m.mergedCluster == mergedCluster
                    && m.similarity == similarity;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return remainingCluster ^ mergedCluster;
    }

    /**
     * Returns the ID of the cluster that was merged into another cluster.
     *
     * @return
     */
    public int mergedCluster() {
        return mergedCluster;
    }

    /**
     * Returns the ID of the clusters into which another cluster was merged,
     * i.e. all the data points in the merged cluster would now have this ID.
     *
     * @return
     */
    public int remainingCluster() {
        return remainingCluster;
    }

    /**
     * Returns the similarity of the two clusters at the time of their merging.
     *
     * @return
     */
    public double similarity() {
        return similarity;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("(").append(mergedCluster).append(" -> ")
                .append(remainingCluster).append(": ").append(similarity).append(")");
        return res.toString();
    }

    @Override
    public int compareTo(Object o) {
        Merge e = (Merge) o;
        if (similarity > e.similarity) {
            return -1;
        } else if (similarity < e.similarity) {
            return 1;
        }
        return 0;
    }

}
