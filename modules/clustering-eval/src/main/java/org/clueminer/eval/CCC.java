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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.math.matrix.Maths;
import org.clueminer.utils.Props;

/**
 * Cubic Clustering Criterion (CCC)
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class CCC<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final String NAME = "CCC";
    private static final long serialVersionUID = 60822019698264781L;

    public CCC() {
        dm = new EuclideanDistance();
    }

    public CCC(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        Dataset<E> dataset = clusters.getLookup().lookup(Dataset.class);
        if (dataset == null) {
            throw new RuntimeException("missing original dataset");
        }
        Matrix X = dataset.asMatrix();
        //a matrix d x d (d - number of attributes)
        // T = X'X
        Matrix TT = X.transpose().times(X);

        //assign matrix - (index, cluster) = 1.0
        Matrix Z = new JMatrix(dataset.size(), clusters.size());
        int k = 0;
        for (Cluster<E> c : clusters) {
            for (E inst : c) {
                Z.set(inst.getIndex(), k, 1.0);
            }
            k++;
        }
        /**
         * TODO: some matrix operations might not be necessary
         * */
        Matrix ZT = Z.transpose();
        // cluster sizes on diagonal -- inverse
        Matrix TIZ = ZT.times(Z).inverse();
        Matrix xbar = TIZ.times(ZT).times(X);
        //xbar.print(3, 3);
        Matrix B = xbar.transpose().times(ZT).times(Z).times(xbar);

        B.print(3, 2);

        //W_q
        Matrix Wq = TT.minus(B);

        double r2 = 1.0 - Wq.trace() / TT.trace();

        System.out.println("r2: " + r2);
        //TODO: implement fully
        return r2;
    }

    /**
     * Alternate computing method
     *
     * @param clusters
     * @return
     */
    public double score2(Clustering<E, C> clusters) {
        return Maths.covariance(withinGroupScatter(clusters)).trace();
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        return score1 > score2;
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public double getMin() {
        return 0;
    }

    @Override
    public double getMax() {
        return Double.POSITIVE_INFINITY;
    }

}
