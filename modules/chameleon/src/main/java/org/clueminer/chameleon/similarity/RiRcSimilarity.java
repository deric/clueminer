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
package org.clueminer.chameleon.similarity;

import org.clueminer.chameleon.Chameleon;
import org.clueminer.chameleon.GraphCluster;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Pair;
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
public class RiRcSimilarity<E extends Instance> extends AbstractSimilarity<E> implements MergeEvaluation<E> {

    public static final String name = "Standard";
    private final Interconnectivity<E> interconnectivity;
    private final Closeness<E> closeness;

    public RiRcSimilarity() {
        interconnectivity = new Interconnectivity<>();
        closeness = new Closeness<>();
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

        double RCL = closeness.getRCL(x, y);
        double closenessPriority = params.getDouble(Chameleon.CLOSENESS_PRIORITY, 2.0);
        //TODO: this is kind of magic. it's not described in original paper. move to another method?
        //give higher similarity to pair of clusters where one cluster is formed by single item (we want to get rid of them)
        //if (a.size() == 1 || b.size() == 1) {
        //    return Math.pow(RCL, closenessPriority) * 40;
        //}

        return interconnectivity.getRIC(x, y) * Math.pow(RCL, closenessPriority);
    }


    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public void clusterCreated(Pair<? extends Cluster<E>> pair, Cluster<E> newCluster, Props params) {
        //nothing to do
    }

}
