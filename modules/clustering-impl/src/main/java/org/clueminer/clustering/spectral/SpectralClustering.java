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
package org.clueminer.clustering.spectral;

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.PriorityQueue;
import org.clueminer.clustering.ClusterHelper;
import org.clueminer.clustering.aggl.AgglClustering;
import org.clueminer.clustering.aggl.Element;
import org.clueminer.clustering.api.AlgParams;
import static org.clueminer.clustering.api.AlgParams.CLUSTERING_TYPE;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpectralClustering<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> implements ClusteringAlgorithm<E, C> {

    public static final String NAME = "Spectral Clustering";
    public static final String K = "k";
    private static final Logger LOG = LoggerFactory.getLogger(SpectralClustering.class);

    //min and max values are used as limit for evolutionary algorithms
    @Param(name = SpectralClustering.K, description = "expected number of clusters", required = true, min = 2, max = 25)
    private int k;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> data, Props params) {
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("The dataset should not be empty");
        }
        distanceFunction = ClusterHelper.initDistance(params);

        k = params.getInt(SpectralClustering.K);
        if (k <= 1) {
            throw new RuntimeException("Number of clusters should be at least 2");
        }

        LOG.debug("{} clustering: {}", getName(), params.toString());
        AbstractQueue<Element> pq = initQueue(triangleSize(data.size()), params);
        Matrix similarityMatrix;
        LOG.debug("computing similarity matrix");
        if (clusterRows(params)) {
            similarityMatrix = AgglClustering.rowSimilarityMatrix(data.asMatrix(), distanceFunction, pq);
        } else {
            LOG.info("matrix columns: {}", data.asMatrix().columnsCount());
            similarityMatrix = AgglClustering.columnSimilarityMatrix(data.asMatrix(), distanceFunction, pq);
        }
        LOG.info("sim matrix rank: {}", similarityMatrix.rank());
        //TODO: implement spectral clustering

        Clustering result = Clusterings.newList(k, data);
        result.setParams(params);
        params.put(AlgParams.ALG, getName());
        result.createCluster(0);

        LOG.debug("result size: {}", result.size());
        return result;
    }

    @Override
    public Configurator<E> getConfigurator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isDeterministic() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Compute size of triangular matrix (n x n) minus diagonal
     *
     * @param n
     * @return
     */
    public int triangleSize(int n) {
        return ((n - 1) * n) >>> 1;
    }

    /**
     * Initialize processing queue
     *
     * @param items expected number of items in the queue
     * @param pref
     * @return
     */
    protected AbstractQueue<Element> initQueue(int items, Props pref) {
        AbstractQueue<Element> pq;
        //by default most similar items have smallest distance
        boolean smallestFirst = pref.getBoolean(AlgParams.SMALLEST_FIRST, true);
        if (smallestFirst) {
            pq = new PriorityQueue<>(items);
        } else {
            //inverse sorting - biggest values first
            Comparator<Element> comp = (Element o1, Element o2) -> o2.compareTo(o1);
            pq = new PriorityQueue<>(items, comp);
        }
        return pq;
    }

    public boolean clusterRows(Props params) {
        if (params.containsKey(CLUSTERING_TYPE)) {
            return ClusteringType.parse(params.getObject(CLUSTERING_TYPE)) != ClusteringType.COLUMNS_CLUSTERING;
        }
        return true; //by default cluster rows
    }

}
