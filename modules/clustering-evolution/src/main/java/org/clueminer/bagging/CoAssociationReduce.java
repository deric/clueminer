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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.aggl.linkage.CompleteLinkage;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringReduce;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.ResultType;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.struct.DendrogramData2;
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

    private static final Logger logger = Logger.getLogger(CoAssociationReduce.class.getName());

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
        HACLW hac = new HACLW();
        //largest values should be merged first
        props.put(AgglParams.SMALLEST_FIRST, false);
        props.put(AgglParams.CLUSTER_ROWS, true);
        props.put(AgglParams.CUTOFF_STRATEGY, "naive cutoff");
        props.put(AgglParams.LINKAGE, CompleteLinkage.name);
        hac.setColorGenerator(cg);
        Dataset<? extends Instance> dataset = c.getLookup().lookup(Dataset.class);

        HierarchicalResult rowsResult = hac.hierarchy(coassoc, dataset, props);
        rowsResult.setResultType(ResultType.ROWS_CLUSTERING);
        rowsResult.getTreeData().print();

        findCutoff(rowsResult, props);
        DendrogramMapping mapping = new DendrogramData2(dataset, rowsResult);

        Clustering clustering = rowsResult.getClustering();
        clustering.mergeParams(props);
        clustering.lookupAdd(mapping);

        return clustering;
    }

    public void findCutoff(HierarchicalResult result, Props params) {
        CutoffStrategy strategy = getCutoffStrategy(params);
        logger.log(Level.FINER, "cutting dendrogram with {0}", strategy.getName());
        double cut = result.findCutoff(strategy);
        logger.log(Level.FINER, "found cutoff {0}, resulting clusters {1}", new Object[]{cut, result.getClustering().size()});
    }

    protected CutoffStrategy getCutoffStrategy(Props params) {
        CutoffStrategy strategy;
        String cutoffAlg = params.get(AgglParams.CUTOFF_STRATEGY, "hill-climb inc");

        if (cutoffAlg.equals("-- naive --")) {
            strategy = CutoffStrategyFactory.getInstance().getDefault();
        } else {
            strategy = CutoffStrategyFactory.getInstance().getProvider(cutoffAlg);
        }
        String evalAlg = params.get(AgglParams.CUTOFF_SCORE, "AIC");
        InternalEvaluator eval = InternalEvaluatorFactory.getInstance().getProvider(evalAlg);
        strategy.setEvaluator(eval);

        return strategy;
    }

}
