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
package org.clueminer.explorer;

import java.util.Comparator;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.EvaluationTable;
import org.openide.nodes.Node;

/**
 * Note the comparator intentionally violates java compare contract. Descending
 * order by default.
 *
 * Evaluators behave correctly, only GUI nodes should be sorted in inverse order
 *
 * @author Tomas Barton
 */
public class ClustComparator implements Comparator<Node> {

    private ClusterEvaluation eval;

    public ClustComparator(ClustSorted children) {
        //
    }

    public ClustComparator(ClusterEvaluation eval) {
        this.eval = eval;
    }

    /**
     * Compare two clustering nodes. We use descending order, therefore better
     * score gets -1 instead of 1.
     *
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(Node o1, Node o2) {
        ClusteringNode c1 = (ClusteringNode) o1;
        ClusteringNode c2 = (ClusteringNode) o2;

        EvaluationTable t1 = c1.evaluationTable(c1.getClustering());
        EvaluationTable t2 = c2.evaluationTable(c2.getClustering());

        //intentionally flip ordering nodes use somehow inversed ordering
        return eval.compare(t2.getScore(eval), t1.getScore(eval));
    }

    public void setEvaluator(ClusterEvaluation eval) {
        this.eval = eval;
    }

}
