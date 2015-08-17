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
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.neighbor.KNNSearch;
import org.clueminer.neighbor.KnnFactory;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Connectivity as criterion for evaluation of clustering quality. As an
 * objective should be minimized. Connectivity should not penalize clusters of
 * arbitrary shapes (different than circles/spheres).
 *
 *
 * @see Handl, Julia, and Joshua Knowles. "An evolutionary approach to
 * multiobjective clustering." Evolutionary Computation, IEEE Transactions on
 * 11.1 (2007): 56-76.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class Connectivity extends AbstractEvaluator {

    private static final long serialVersionUID = 5416705978468100914L;
    private static final String name = "Connectivity";
    private static final String PARAM = "connectivity.L";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        double conn = 0.0;
        Dataset<? extends Instance> dataset = clusters.getLookup().lookup(Dataset.class);
        if (dataset == null) {
            throw new RuntimeException("missing dataset");
        }

        //parameter specifing number of neighbours that contribute to connectivity
        // value 10 is suggested by Handl, Knowles
        int L = params.getInt(PARAM, 10);
        KNNSearch knn = KnnFactory.getInstance().getDefault();
        if (knn == null) {
            throw new RuntimeException("missing k-nn implementation");
        }
        Cluster c;
        Neighbor[] nn;
        knn.setDataset(dataset);
        for (int i = 0; i < clusters.size(); i++) {
            c = clusters.get(i);
            for (int j = 0; j < c.size(); j++) {
                nn = knn.knn(c.get(i), L, params);
                for (int k = 0; k < L; k++) {
                    if (c.contains(nn[k].index)) {
                        conn += 1.0 / (k + 1);
                    }
                }
            }
        }
        return conn;
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        return score1 > score2;
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    /**
     *
     * @return worst possible value
     */
    @Override
    public double getMin() {
        return 0;
    }

    @Override
    public double getMax() {
        return Double.POSITIVE_INFINITY;
    }

}
