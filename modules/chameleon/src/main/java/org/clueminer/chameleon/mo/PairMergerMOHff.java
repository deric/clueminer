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
package org.clueminer.chameleon.mo;

import java.util.ArrayList;
import org.clueminer.chameleon.GraphCluster;
import org.clueminer.clustering.api.MergeEvaluation;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = Merger.class)
public class PairMergerMOHff<E extends Instance, C extends GraphCluster<E>, P extends MoPair<E, C>> extends PairMergerMOH<E, C, P> implements Merger<E> {

    public static final String NAME = "MOM-HS-no-filter";

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Generate all possible (unique) combinations of pairs
     * - complexity O(n^2) = O(n * (n-1) / 2)
     *
     * @param queue
     * @param pref
     */
    @Override
    protected void fillQueue(FrontHeapQueue<E, C, P> queue, Props pref) {
        C c1, c2;
        //generate all pairs
        for (int i = 0; i < clusters.size(); i++) {
            c1 = (C) clusters.get(i);
            for (int j = 0; j < i; j++) {
                c2 = (C) clusters.get(j);
                queue.add((P) createPair(c1, c2, pref));
            }
        }
    }

    /**
     * TODO: could be safely if we had a Queue interface
     *
     * @param cluster
     * @param pref
     */
    protected void singleMerge(P curr, Props pref, int debug) {
        if (debug > 1) {
            System.out.println("merging: [" + curr.A.getClusterId() + ", " + curr.B.getClusterId() + "] " + curr.toString());
        }
        int i = curr.A.getClusterId();
        int j = curr.B.getClusterId();
        while (blacklist.contains(i) || blacklist.contains(j)) {
            curr = queue.poll();
            i = curr.A.getClusterId();
            j = curr.B.getClusterId();
        }
        blacklist.add(i);
        blacklist.add(j);
        if (i == j) {
            throw new RuntimeException("Cannot merge two same clusters");
        }
        //System.out.println("merging: [" + curr.A.getClusterId() + ", " + curr.B.getClusterId() + "] " + curr.getValue());
        //System.out.println("   " + curr.toString());
        ArrayList<Node<E>> clusterNodes = (ArrayList<Node<E>>) curr.A.getNodes().clone();
        clusterNodes.addAll(curr.B.getNodes());

        GraphCluster<E> newCluster = new GraphCluster(clusterNodes, graph, clusters.size(), bisection, pref);
        clusters.add(newCluster);
        for (MergeEvaluation<E> me : objectives) {
            me.clusterCreated(curr, newCluster, pref);
        }
        //eval.clusterCreated(curr, newCluster, pref);
        addIntoTree((MoPair<E, GraphCluster<E>>) curr, pref);
        updateExternalProperties(newCluster, curr.A, curr.B);
        addIntoQueue((C) newCluster, pref);
        //remove any pair containing merged items from current fronts
        queue.filterOut();
    }

    /**
     * TODO: could be safely if we had a Queue interface
     *
     * @param cluster
     * @param pref
     */
    private void addIntoQueue(C cluster, Props pref) {
        for (int i = 0; i < cluster.getClusterId(); i++) {
            if (!blacklist.contains(i)) {
                //System.out.println("adding pair [" + cluster.getClusterId() + ", " + clusters.get(i).getClusterId() + "]");
                queue.add((P) createPair((C) clusters.get(i), cluster, pref));
            }
        }
    }

}
