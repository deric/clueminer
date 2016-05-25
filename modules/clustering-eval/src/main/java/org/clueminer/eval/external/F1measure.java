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
package org.clueminer.eval.external;

import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.utils.PairMatch;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * F-measure or F-score
 *
 * @see http://en.wikipedia.org/wiki/F1_score
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class F1measure extends AbstractCountingPairs {

    private static final String name = "F-measure";
    private static final long serialVersionUID = 5075558180348805172L;
    private double beta = 1.0;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countScore(PairMatch pm, Props params) {
        double b = params.getDouble("beta", beta);
        double squareBeta = Math.pow(b, 2);
        return (1 + squareBeta) * pm.tp / ((1.0 + squareBeta) * pm.tp + squareBeta * pm.fn + pm.fp);
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }
}
