/*
 * Copyright (C) 2011-2017 clueminer.org
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
