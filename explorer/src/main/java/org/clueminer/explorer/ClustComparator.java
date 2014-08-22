package org.clueminer.explorer;

import java.util.Comparator;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.EvaluationTable;

/**
 *
 * @author Tomas Barton
 */
public class ClustComparator implements Comparator<ClusteringNode> {

    private ClusterEvaluator eval;

    public ClustComparator(ClusterEvaluator eval) {
        this.eval = eval;
    }

    @Override
    public int compare(ClusteringNode o1, ClusteringNode o2) {

        EvaluationTable t1 = o1.evaluationTable(o1.getClustering());
        EvaluationTable t2 = o2.evaluationTable(o2.getClustering());

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
