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

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * This class implements the improved standard similarity measure proposed in
 * http://subs.emis.de/LNI/Proceedings/Proceedings107/gi-proc-107-015.pdf.
 *
 * Internal properties of the newly created are instantly determined from the
 * external and internal properties of the clusters being merged.
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = MergeEvaluation.class)
public class ShatovskaSimilarity<E extends Instance> implements MergeEvaluation<E> {

    private static final String name = "Shatovska";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Cluster<E> a, Cluster<E> b, Props params) {
        if (!(a instanceof GraphCluster) || !(b instanceof GraphCluster)) {
            throw new RuntimeException("clusters must contain a graph structure to evaluate similarity");
        }
        GraphCluster<E> x, y;
        if (b.getClusterId() > a.getClusterId()) {
            x = (GraphCluster<E>) b;
            y = (GraphCluster<E>) a;
        } else {
            x = (GraphCluster<E>) a;
            y = (GraphCluster<E>) b;
        }

        double closenessPriority = params.getDouble(Chameleon.CLOSENESS_PRIORITY, 2.0);
        GraphPropertyStore gps = getGraphPropertyStore(x);
        int i = x.getClusterId();
        int j = y.getClusterId();
        double ec1 = x.getEdgeCount();
        double ec2 = y.getEdgeCount();
        //give higher similarity to pair of clusters where one cluster is formed by single item (we want to get rid of them)
        if (ec1 == 0 || ec2 == 0) {
            return gps.getECL(i, j) * 40;
        }

        double val = (gps.getCnt(i, j) / (Math.min(ec1, ec2)))
                * Math.pow((gps.getECL(i, j) / ((x.getACL() * ec1) / (ec1 + ec2)
                        + (y.getACL() * ec2) / (ec1 + ec2))), closenessPriority)
                * Math.pow((Math.min(x.getACL(), y.getACL()) / Math.max(x.getACL(), y.getACL())), 1);

        return val;

    }

    private GraphPropertyStore getGraphPropertyStore(GraphCluster<E> clust) {
        Graph g = clust.getGraph();
        GraphPropertyStore gps = g.getLookup().lookup(GraphPropertyStore.class);
        if (gps == null) {
            throw new RuntimeException("graph property store was not found");
        }
        return gps;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

}
