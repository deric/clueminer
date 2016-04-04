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
package org.clueminer.chameleon.similarity;

import org.clueminer.chameleon.GraphCluster;
import org.clueminer.chameleon.GraphPropertyStore;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;

/**
 *
 * @author deric
 * @param <E>
 */
public class AbstractSimilarity<E extends Instance> {

    /**
     * Fetches graph from a GraphCluster instance
     *
     * @param clust
     * @return
     */
    public GraphPropertyStore getGraphPropertyStore(GraphCluster<E> clust) {
        Graph g = clust.getGraph();
        GraphPropertyStore gps = g.getLookup().lookup(GraphPropertyStore.class);
        if (gps == null) {
            throw new RuntimeException("graph property store was not found");
        }
        return gps;
    }

    public void checkClusters(Cluster<E> a, Cluster<E> b) {
        if (!(a instanceof GraphCluster) || !(b instanceof GraphCluster)) {
            throw new RuntimeException("clusters must contain a graph structure to evaluate similarity");
        }
    }

}
