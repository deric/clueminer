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
package org.clueminer.chameleon.similarity;

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

    public static final String NAME = "Standard-fixed";
    private final Interconnectivity<E> interconnectivity;
    private final Closeness<E> closeness;

    public RiRcSimilarity() {
        interconnectivity = new Interconnectivity<>();
        closeness = new Closeness<>();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Cluster<E> a, Cluster<E> b, Props params) {
        checkClusters(a, b);

        return interconnectivity.score(a, b, params) * closeness.score(a, b, params);
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
