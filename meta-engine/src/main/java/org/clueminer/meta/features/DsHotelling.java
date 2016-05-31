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
package org.clueminer.meta.features;

import java.util.HashMap;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.StatsNum;
import org.clueminer.math.Matrix;
import org.clueminer.math.Vector;
import org.clueminer.math.impl.DenseVector;
import org.clueminer.math.matrix.SymmetricMatrixDiag;
import org.clueminer.meta.api.DataStats;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class DsHotelling<E extends Instance> implements DataStats<E> {

    public static final String T2 = "t2";

    @Override
    public String[] provides() {
        return new String[]{T2};
    }

    /**
     * Sample covariance
     *
     * @param dataset
     * @return
     */
    protected double covariance(Dataset<E> dataset) {
        Matrix m = dataset.asMatrix();
        Matrix cov = new SymmetricMatrixDiag(m.columnsCount());

        DenseVector mean = new DenseVector(dataset.attributeCount());
        for (int i = 0; i < mean.size(); i++) {
            mean.set(i, dataset.getAttribute(i).statistics(StatsNum.MEAN));
        }

        Vector v;
        double res, sum = 0.0;
        for (int i = 0; i < m.rowsCount(); i++) {
            v = m.getRowVector(i).minus(mean);
            res = v.dot(v);
            sum += res;
        }
        return sum / (m.rowsCount() - 1);


        /* for (int i = 0; i < m.columnsCount(); i++) {            mean = dataset.getAttribute(i).statistics(StatsNum.AVG);
            cov.set(i, i, dataset.getAttribute(i).statistics(StatsNum.VARIANCE));
            for (int j = 0; j < i; j++) {
                //cov.set(i, j, mean);
            }
        } */

        //return cov;
    }

    @Override
    public double evaluate(Dataset<E> dataset, String feature, Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void computeAll(Dataset<E> dataset, HashMap<String, Double> features, Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
