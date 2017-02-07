/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.clustering.gui;

import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.clustering.api.ConfiguratorFactory;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.struct.DendrogramData;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.flow.api.AbsFlowNode;
import org.clueminer.flow.api.FlowError;
import org.clueminer.flow.api.FlowNode;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Node responsible for clustering algorithm execution.
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = FlowNode.class)
public class ClusteringFlow<E extends Instance, C extends Cluster<E>> extends AbsFlowNode implements FlowNode {

    private static final String NAME = "clustering";
    private ColorGenerator cg;
    protected ClusteringAlgorithm<E, C> algorithm;
    private static final Logger LOG = LoggerFactory.getLogger(ClusteringFlow.class);

    public ClusteringFlow() {
        inputs = new Class[]{Dataset.class};
        outputs = new Class[]{Clustering.class, DendrogramMapping.class};
        panel = new ClusteringUI();
    }

    public ClusteringAlgorithm<E, C> getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(ClusteringAlgorithm<E, C> algorithm) {
        this.algorithm = algorithm;
    }

    protected CutoffStrategy<E, C> getCutoffStrategy(Props params) {
        CutoffStrategy<E, C> strategy;
        String cutoffAlg = params.get(AlgParams.CUTOFF_STRATEGY, "hill-climb inc");

        if (cutoffAlg.equals("-- naive --")) {
            strategy = CutoffStrategyFactory.getInstance().getDefault();
        } else {
            strategy = CutoffStrategyFactory.getInstance().getProvider(cutoffAlg);
        }
        String evalAlg = params.get(AlgParams.CUTOFF_SCORE, "AIC");
        InternalEvaluatorFactory<E, C> ief = InternalEvaluatorFactory.getInstance();
        InternalEvaluator<E, C> eval = ief.getProvider(evalAlg);
        strategy.setEvaluator(eval);

        return strategy;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object[] execute(Object[] inputs, Props params) throws FlowError {
        checkInputs(inputs);
        Object[] ret = new Object[2];
        Dataset<E> dataset = (Dataset<E>) inputs[0];
        checkInput(dataset);

        DendrogramMapping dendroData;
        if (params.getObject(AlgParams.CLUSTERING_TYPE) == ClusteringType.ROWS_CLUSTERING) {
            HierarchicalResult res = hclustRows(dataset, params);
            dendroData = new DendrogramData(dataset, res);
        } else {
            dendroData = clusterAll(dataset, params);
        }

        ret[0] = clusterRows(dataset, params);
        ret[1] = dendroData;
        return ret;
    }

    private void updateAlgorithm(Props params) {
        if (params.containsKey(AlgParams.ALG)) {
            String alg = params.get(AlgParams.ALG);
            if (algorithm != null && algorithm.getName().equals(alg)) {
                return;
            }
            ClusteringFactory cf = ClusteringFactory.getInstance();
            this.algorithm = cf.getProvider(alg);
            if (cg != null) {
                this.algorithm.setColorGenerator(cg);
            }
        }
    }

    /**
     * Cluster both - rows and columns
     *
     * @param dataset data to be clustered
     * @param params
     * @return
     */
    public DendrogramMapping clusterAll(Dataset<E> dataset, Props params) {
        updateAlgorithm(params);
        HierarchicalResult rowsResult = hclustRows(dataset, params);
        findCutoff(rowsResult, params);
        HierarchicalResult columnsResult = hclustColumns(dataset, params);
        DendrogramMapping mapping = new DendrogramData(dataset, rowsResult, columnsResult);
        Clustering clustering = rowsResult.getClustering();
        clustering.lookupAdd(mapping);
        clustering.lookupAdd(rowsResult);
        clustering.lookupAdd(columnsResult);
        clustering.mergeParams(params);
        return mapping;
    }

    public Clustering<E, C> clusterRows(Dataset<E> dataset, Props params) {
        Clustering clustering;
        updateAlgorithm(params);
        if (algorithm instanceof AgglomerativeClustering) {
            HierarchicalResult rowsResult = hclustRows(dataset, params);

            findCutoff(rowsResult, params);
            DendrogramMapping mapping = new DendrogramData(dataset, rowsResult);

            clustering = rowsResult.getClustering();
            clustering.mergeParams(params);
            clustering.lookupAdd(mapping);
        } else {
            //non-hierarchical method
            boolean needsConfiguration = isEstimationNeeded(algorithm, params);
            Configurator config;
            if (params.containsKey(ConfiguratorFactory.CONFIG)) {
                config = ConfiguratorFactory.getInstance().getProvider(params.get(ConfiguratorFactory.CONFIG));
            } else {
                config = algorithm.getConfigurator();
            }
            if (needsConfiguration) {
                config.configure(dataset, params);
                LOG.info("estimated parameters: {} for {}", params.toJson(), algorithm.getName());
            } else {
                LOG.info("skipping parameters estimation. all required parameters were specified");
            }

            clustering = algorithm.cluster(dataset, params);
        }
        return clustering;
    }

    public HierarchicalResult hclustColumns(Dataset<E> dataset, Props params) {
        params.put(AlgParams.CLUSTERING_TYPE, ClusteringType.COLUMNS_CLUSTERING);
        AgglomerativeClustering aggl = (AgglomerativeClustering) algorithm;
        HierarchicalResult columnsResult = aggl.hierarchy(dataset, params);
        //treeOrder.optimize(columnsResult, true);
        //CutoffStrategy strategy = getCutoffStrategy(params);
        //columnsResult.findCutoff(strategy);
        return columnsResult;
    }

    private boolean isEstimationNeeded(ClusteringAlgorithm alg, Props params) {
        for (Parameter p : alg.getRequiredParameters()) {
            if (p.isRequired() && !params.containsKey(p.getName())) {
                return true;
            }
        }
        return false;
    }

    public void findCutoff(HierarchicalResult result, Props params) {
        CutoffStrategy strategy = getCutoffStrategy(params);
        LOG.info("cutting dendrogram with {}", strategy.getName());
        double cut = result.findCutoff(strategy);
        LOG.debug("found cutoff {}, resulting clusters {}", cut, result.getClustering().size());
    }

    public HierarchicalResult hclustRows(Dataset<E> dataset, Props params) {
        params.put(AlgParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        LOG.info("clustering {}", params.toString());
        AgglomerativeClustering aggl = (AgglomerativeClustering) algorithm;
        LOG.info("algorithm is {}", algorithm.getName());
        LOG.info("dataset: {}", dataset.getName());
        HierarchicalResult rowsResult = aggl.hierarchy(dataset, params);
        //TODO: tree ordering might break assigning items to clusters
        //treeOrder.optimize(rowsResult, true);
        return rowsResult;
    }

    private void checkInput(Dataset<? extends Instance> dataset) {
        if (dataset == null || dataset.isEmpty()) {
            throw new NullPointerException("no data to process");
        }
    }

}
