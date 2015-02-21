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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.KNN;
import org.clueminer.distance.api.KnnFactory;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 * Connectivity as criterion for evaluation of clustering quality
 *
 * @see Ding, Chris, and Xiaofeng He. "K-nearest-neighbor consistency in data
 * clustering: incorporating local information into global optimization."
 * Proceedings of the 2004 ACM symposium on Applied computing. ACM, 2004.
 *
 * @see Handl, Julia, and Joshua Knowles. "An evolutionary approach to
 * multiobjective clustering." Evolutionary Computation, IEEE Transactions on
 * 11.1 (2007): 56-76.
 *
 * @author Tomas Barton
 */
public class Connectivity extends AbstractEvaluator {

    private static final long serialVersionUID = 5416705978468100914L;
    private static final String name = "Connectivity";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset) {
        double conn = 0.0;
        //parameter specifing number of neighbours that contribute to connectivity
        int L = 10;
        KNN knn = KnnFactory.getInstance().getDefault();
        if (knn == null) {
            throw new RuntimeException("missing k-nn implementation");
        }
        Props params = new Props();
        Cluster c;
        Instance[] nn;
        for (int i = 0; i < clusters.size(); i++) {
            c = clusters.get(i);
            for (int j = 0; j < c.size(); j++) {
                nn = knn.nn(j, L, dataset, params);
                for (int k = 0; k < L; k++) {
                    if (c.contains(nn[k].getIndex())) {
                        conn += 1 / (double) k;
                    }
                }
            }
        }

        return conn;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isMaximized() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
