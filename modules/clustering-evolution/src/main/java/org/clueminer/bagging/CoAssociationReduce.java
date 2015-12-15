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
import org.clueminer.clustering.aggl.HC;
import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Consensus;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.struct.DendrogramData;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.std.StdScale;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Inspired by Jain's evidence accumulation
 *
 * Co-association matrix counts number of assignments of the instance to same
 * cluster
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = Consensus.class)
public class CoAssociationReduce<E extends Instance, C extends Cluster<E>> extends CoAssocMatrix<E, C> implements Consensus<E, C> {

    private static final Logger logger = Logger.getLogger(CoAssociationReduce.class.getName());
    public static final String name = "co-association HAC";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<E, C> reduce(Clustering[] clusts, Algorithm alg,
            ColorGenerator cg, Props props) {
        Matrix coassoc = createMatrix(clusts);

        //typically hierarchical clustering looks for minimal distance
        StdScale std = new StdScale();
        double val;
        for (int i = 0; i < coassoc.rowsCount(); i++) {
            for (int j = 0; j < i; j++) {
                //inverse distance
                val = std.scaleToRange(coassoc.get(i, j), 0.0, clusts.length, 1.0, 0);
                coassoc.set(i, j, val);
            }
        }

        //coassoc.printLower(2, 3);
        HC hac = new HCLW();
        //largest values should be merged first
        //props.put(AgglParams.SMALLEST_FIRST, false);
        props.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        //props.put(AgglParams.CUTOFF_STRATEGY, "hill-climb inc");
        props.put(AgglParams.CUTOFF_STRATEGY, "hill-climb cutoff");
        //props.put(AgglParams.LINKAGE, CompleteLinkage.name);
        //props.put(AgglParams.LINKAGE, MedianLinkage.name);
        hac.setColorGenerator(cg);
        Dataset<E> dataset = clusts[0].getLookup().lookup(Dataset.class);

        HierarchicalResult rowsResult = hac.hierarchy(coassoc, dataset, props);
        //rowsResult.setResultType(ClusteringType.ROWS_CLUSTERING);
        // rowsResult.getTreeData().print();
        rowsResult.setProximityMatrix(coassoc);

        findCutoff(rowsResult, props);
        DendrogramMapping mapping = new DendrogramData(dataset, rowsResult);

        Clustering<E, C> clustering = rowsResult.getClustering();
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
        InternalEvaluator<E, C> eval = (InternalEvaluator<E, C>) InternalEvaluatorFactory.getInstance().getProvider(evalAlg);
        strategy.setEvaluator(eval);

        return strategy;
    }

}
