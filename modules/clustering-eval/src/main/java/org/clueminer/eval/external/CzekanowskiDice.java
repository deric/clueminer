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
import org.openide.util.lookup.ServiceProvider;

/**
 * The Czekanowski (1932), Dice (1945) -- a.k.a. the Ochai index
 *
 * The Czekanowski index is the harmonic mean of the precision and recall
 * coefficients, thus could be equal to F-measure (for beta = 1.0)
 *
 * C = 2 * (precision * recall) / (precision + recall)
 *
 * @author deric
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class CzekanowskiDice extends AbstractCountingPairs {

    private static final long serialVersionUID = 8647620533572167034L;
    private static final String name = "Czekanowski-Dice";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countScore(PairMatch pm) {
        return (2 * pm.tp) / (double) (2 * pm.tp + pm.fn + pm.fp);
    }

}
