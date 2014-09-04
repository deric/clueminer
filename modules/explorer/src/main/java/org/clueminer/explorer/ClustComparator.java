package org.clueminer.explorer;

import java.util.Comparator;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.EvaluationTable;
import org.openide.nodes.Node;

/**
 *
 * @author Tomas Barton
 */
public class ClustComparator implements Comparator<Node> {

    private ClusterEvaluation eval;
    private boolean ascOrder = false;

    public ClustComparator(ClusterEvaluation eval) {
        this.eval = eval;
    }

    public ClustComparator(ClusterEvaluation eval, boolean ascendingOrder) {
        this.eval = eval;
        this.ascOrder = ascendingOrder;
    }

    /**
     * Compare two clustering nodes. We use descending order, therefore better
     * score gets -1 instead of 1.
     *
     * @TODO consider parameter for inverting the ordering
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

        double s1 = t1.getScore(eval);
        double s2 = t2.getScore(eval);

        if (s1 == s2) {
            return 0;
        }

        // "best" solution is at the end
        if (ascOrder) {
            if (eval.compareScore(s1, s2)) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if (eval.compareScore(s1, s2)) {
                return -1;
            } else {
                return 1;
            }
        }

    }

    public void setEvaluator(ClusterEvaluation eval) {
        this.eval = eval;
    }

    public boolean isAscOrder() {
        return ascOrder;
    }

    public void setAscOrder(boolean ascOrder) {
        this.ascOrder = ascOrder;
    }

}
