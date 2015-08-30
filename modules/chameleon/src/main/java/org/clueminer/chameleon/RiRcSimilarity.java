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
package org.clueminer.chameleon;

import java.util.LinkedList;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Relative Interconnectivity + Relative Closeness similarity (dynamic modeling
 * framework from Chameleon). An underlying graph structure is required for
 * computing this metric.
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = MergeEvaluation.class)
public class RiRcSimilarity<E extends Instance> extends PairMerger<E> implements MergeEvaluation<E> {

    public static final String name = "Standard";

    public RiRcSimilarity() {
    }

    public RiRcSimilarity(Graph g, Bisection bisection) {
        super(g, bisection);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Cluster<E> a, Cluster<E> b, Props params) {
        checkClusters(a, b);
        GraphCluster<E> x = (GraphCluster<E>) a;
        GraphCluster<E> y = (GraphCluster<E>) b;
        double RIC = getRIC(x, y);
        double RCL = getRCL(x, y);
        double closenessPriority = params.getDouble(Chameleon.CLOSENESS_PRIORITY, 2.0);
        //give higher similarity to pair of clusters where one cluster is formed by single item (we want to get rid of them)
        if (a.size() == 1 || b.size() == 1) {
            return RIC * Math.pow(RCL, closenessPriority) * 40;
        }

        return RIC * Math.pow(RCL, closenessPriority);
    }

    /**
     * Compute relative interconnectivity
     *
     * @param x
     * @param y
     * @return
     */
    protected double getRIC(GraphCluster<E> x, GraphCluster<E> y) {
        GraphPropertyStore gps = getGraphPropertyStore(x);
        double eic = gps.getEIC(x.getClusterId(), y.getClusterId());
        return eic / ((x.getIIC() + y.getIIC()) / 2);
    }

    /**
     * Compute relative closeness
     *
     * @param x
     * @param y
     * @return
     */
    protected double getRCL(GraphCluster<E> x, GraphCluster<E> y) {
        double nc1 = x.size();
        double nc2 = y.size();
        GraphPropertyStore gps = getGraphPropertyStore(x);
        double ecl = gps.getECL(x.getClusterId(), y.getClusterId());

        return ecl / ((nc1 / (nc1 + nc2)) * x.getICL() + (nc2 / (nc1 + nc2)) * y.getICL());
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

    @Override
    public Cluster<E> createNewCluster(Cluster<E> a, Cluster<E> b, Props params) {
        checkClusters(a, b);
        GraphCluster cluster1 = (GraphCluster) a;
        GraphCluster cluster2 = (GraphCluster) b;
        LinkedList<Node> clusterNodes = cluster1.getNodes();
        clusterNodes.addAll(cluster2.getNodes());
        addIntoTree(cluster1, cluster2, params);
        GraphCluster newCluster = new GraphCluster(clusterNodes, graph, clusterCount++, bisection);
        clusters.add(newCluster);
        return newCluster;
    }

    private void checkClusters(Cluster<E> a, Cluster<E> b) {
        if (!(a instanceof GraphCluster) || !(b instanceof GraphCluster)) {
            throw new RuntimeException("clusters must contain a graph structure to evaluate similarity");
        }
    }

}
