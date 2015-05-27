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
package org.clueminer.bagging;

import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.aggl.HacLwMsPar;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringReduce;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.SymmetricMatrix;
import org.clueminer.utils.Props;

/**
 * Inspired by Jain's evidence accumulation
 *
 * @author deric
 */
public class CoAssociationReduce implements ClusteringReduce {

    @Override
    public Clustering<? extends Cluster> reduce(Clustering[] clusts, AbstractClusteringAlgorithm alg, ColorGenerator cg, Props props) {
        Clustering c = clusts[0];
        //total number of items
        int n = c.instancesCount();
        Matrix coassoc = new SymmetricMatrix(n, n);
        Instance a, b;
        //cluster membership
        int ca, cb;
        double value;
        int x = 0;
        for (Clustering clust : clusts) {
            System.out.println("reducing " + (x++));
            for (int i = 1; i < n; i++) {
                a = clust.instance(i);
                ca = clust.assignedCluster(a.getIndex());
                for (int j = 0; j < i; j++) {
                    b = clust.instance(j);
                    //for each pair of instances check if placed in the same cluster
                    cb = clust.assignedCluster(b.getIndex());
                    if (ca == cb) {
                        value = coassoc.get(i, j) + 1.0;
                        coassoc.set(i, j, value);
                    }
                }
            }
        }
        //coassoc.printLower(2, 3);
        HACLW hac = new HacLwMsPar(4);
        //largest values should be merged first
        props.put(AgglParams.SMALLEST_FIRST, false);
        HierarchicalResult res = hac.hierarchy(coassoc, c.getLookup().lookup(Dataset.class), props);
        return res.getClustering();
    }

}
