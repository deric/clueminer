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
package org.clueminer.clustering.aggl.linkage;

import java.util.Set;
import org.clueminer.clustering.api.AbstractLinkage;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;

/**
 * Inverse distance measuring (bigger is better)
 *
 * @author deric
 * @param <E>
 */
//@ServiceProvider(service = ClusterLinkage.class)
public class CompleteLinkageInv<E extends Instance> extends AbstractLinkage<E> implements ClusterLinkage<E> {

    public static final String name = "Complete-inv_dist";
    private static final long serialVersionUID = -1863699488371017773L;

    public CompleteLinkageInv() {
        super(EuclideanDistance.getInstance());
    }

    public CompleteLinkageInv(Distance dm) {
        super(dm);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double distance(Cluster<E> cluster1, Cluster<E> cluster2) {
        double minDist = Double.MAX_VALUE;
        for (Instance i : cluster1) {
            for (Instance j : cluster2) {
                double s = distanceMeasure.measure(i, j);
                if (!distanceMeasure.compare(minDist, s)) {
                    minDist = s;
                }
            }
        }
        return minDist;
    }

    /**
     * Inverse distance measuring (bigger is better)
     *
     * @param similarityMatrix
     * @param cluster
     * @param toAdd
     * @return
     */
    @Override
    public double similarity(Matrix similarityMatrix, Set<Integer> cluster, Set<Integer> toAdd) {
        double minDist = Double.MAX_VALUE;
        for (int i : cluster) {
            for (int j : toAdd) {
                double s = similarityMatrix.get(i, j);
                if (!distanceMeasure.compare(minDist, s)) {
                    minDist = s;
                }
            }
        }
        return minDist;
    }

    @Override
    public double alphaA(int ma, int mb, int mq) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double alphaB(int ma, int mb, int mq) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double beta(int ma, int mb, int mq) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double gamma() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
