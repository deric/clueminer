package org.clueminer.explorer;

import java.util.Comparator;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.EvaluationTable;
import org.openide.nodes.Node;

/**
 *
 * @author Tomas Barton
 */
public class ClustComparator implements Comparator<Node> {

    private ClusterEvaluator eval;

    public ClustComparator(ClusterEvaluator eval) {
        this.eval = eval;
    }

    @Override
    public int compare(Node o1, Node o2) {
        ClusteringNode c1 = (ClusteringNode) o1;
        ClusteringNode c2 = (ClusteringNode) o2;

        EvaluationTable t1 = c1.evaluationTable(c1.getClustering());
        EvaluationTable t2 = c2.evaluationTable(c2.getClustering());

        double s1 = t1.getScore(eval);
        double s2 = t2.getScore(eval);

        if (s1 == s2) {
            return 0;
        }

        if (eval.compareScore(s1, s2)) {
            return 1;
        } else {
            return -1;
        }
    }

}
