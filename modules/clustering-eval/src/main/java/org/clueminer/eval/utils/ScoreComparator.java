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

    public ScoreComparator() {

    }

    public ScoreComparator(ClusterEvaluation eval) {
        this.evaluator = eval;
    }

    public void setEvaluator(ClusterEvaluation evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public int compare(Numeric s1, Numeric s2) {
        return evaluator.compare(s1.getValue(), s2.getValue());
    }

}
