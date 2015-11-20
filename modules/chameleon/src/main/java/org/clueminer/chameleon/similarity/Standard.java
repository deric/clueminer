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
 * Similarity metric as defined by Karypis et al.
 *
 * Karypis, George, Eui-Hong Han, and Vipin Kumar. "Chameleon: Hierarchical
 * clustering using dynamic modeling." Computer 32.8 (1999): 68-75.
 *
 * @author deric
 */
@ServiceProvider(service = MergeEvaluation.class)
public class Standard<E extends Instance> extends AbstractSimilarity<E> implements MergeEvaluation<E> {

    public static final String name = "Standard";
    private final Interconnectivity<E> interconnectivity;
    private final Closeness<E> closeness;

    public Standard() {
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

        return interconnectivity.score(a, b, params) * Math.pow(RCL, closenessPriority);
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
