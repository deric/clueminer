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
 * Accuracy
 *
 * @see http://en.wikipedia.org/wiki/Accuracy_and_precision for definition
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class Accuracy extends AbstractCountingPairs {

    private static final long serialVersionUID = -7408696944704938976L;
    private static final String NAME = "Accuracy";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double countScore(PairMatch pm, Props params) {
        return (pm.tp + pm.tn) / (double) (pm.tp + pm.fn + pm.fp + pm.tn);
    }

}
