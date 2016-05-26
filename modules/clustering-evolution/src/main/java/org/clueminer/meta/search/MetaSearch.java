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
package org.clueminer.meta.search;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.evolution.BaseEvolution;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.Individual;
import org.clueminer.meta.api.DataStats;
import org.clueminer.meta.api.DataStatsFactory;
import org.clueminer.utils.Props;
import org.openide.util.Lookup;

/**
 * Uses meta-features to discover suitable clustering algorithm
 *
 * @author deric
 */
public class MetaSearch<E extends Instance, C extends Cluster<E>> extends BaseEvolution implements Runnable, Evolution, Lookup.Provider {

    private static final String NAME = "Meta search";
    private static final Logger logger = Logger.getLogger(MetaSearch.class.getName());

    protected final Executor exec;
    protected int gen;
    private List<Distance> dist;
    protected List<ClusterLinkage> linkage;
    protected List<CutoffStrategy> cutoff;
    protected List<InternalEvaluator<E, C>> evaluators;
    protected int cnt;

    public MetaSearch() {
        super();
        exec = new ClusteringExecutorCached();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Individual createIndividual() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        evolutionStarted(this);
        prepare();
        InternalEvaluatorFactory<E, C> ief = InternalEvaluatorFactory.getInstance();
        evaluators = ief.getAll();

        if (ph != null) {
            int workunits = 5;
            logger.log(Level.INFO, "search workunits: {0}", workunits);
            ph.start(workunits);
        }

        if (!config.containsKey(AgglParams.STD)) {
            config.put(AgglParams.STD, "z-score");
        }
        Dataset<E> data = standartize(config);
        HashMap<String, Double> meta = computeMeta(data, config);
        logger.log(Level.INFO, "got {0} meta parameters", meta.size());

        cnt = 0;

        finish();
    }

    private HashMap<String, Double> computeMeta(Dataset<E> data, Props config) {
        DataStatsFactory dsf = DataStatsFactory.getInstance();
        HashMap<String, Double> meta = new HashMap<>();
        double v;
        for (DataStats<E> ds : dsf.getAll()) {
            v = ds.evaluate(data);
            meta.put(ds.getName(), v);
            logger.log(Level.INFO, "meta {0} = {1}", new Object[]{ds.getName(), v});
        }
        return meta;
    }

}
