/*
 * Copyright (C) 2011-2019 clueminer.org
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
package org.clueminer.explorer;

import java.util.Comparator;
import java.util.List;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Rank;
import org.clueminer.eval.sort.MORank;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/**
 *
 * @author deric
 */
public class ClustComparatorMO extends ClustComparator implements Comparator<Node> {

    protected ClustSorted clusteringNodes;
    private List<ClusterEvaluation> objectives;
    private Rank rank;
    private static final RequestProcessor RP = new RequestProcessor("MO sort", 5, false);

    public ClustComparatorMO(ClustSorted sorted) {
        super(sorted);
        this.clusteringNodes = sorted;
        rank = new MORank();
    }

    public void sort() {
        Clustering[] clusterings = new Clustering[0];
        //TODO: extract array of clusterings
        rank.sort(clusterings, objectives);
    }

}
