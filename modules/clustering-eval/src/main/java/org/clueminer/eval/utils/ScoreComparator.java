package org.clueminer.eval.utils;

import java.util.Comparator;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.math.Numeric;

/**
 *
 * @author Tomas Barton
 */
public class ScoreComparator implements Comparator<Numeric> {

    private ClusterEvaluation evaluator;
    private boolean asc = true;

    public ScoreComparator() {

    }

    public ScoreComparator(ClusterEvaluation eval) {
        this.evaluator = eval;
    }

    public void setEvaluator(ClusterEvaluation evaluator) {
        this.evaluator = evaluator;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    @Override
    public int compare(Numeric s1, Numeric s2) {
        boolean bigger;

        if (s1.compareTo(s2) == 0) {
            return 0;
        }
        bigger = evaluator.isBetter(s1.getValue(), s2.getValue());

        if (!asc) {
            bigger = !bigger;
        }
        // "best" solution is at the end
        if (bigger) {
            return 1;
        } else {
            return -1;
        }
    }

}
