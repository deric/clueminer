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
 * The Phi index is a classical measure of the correlation between two
 * dichotomic variables.
 *
 * @author deric
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class Phi extends AbstractCountingPairs {

    private static final long serialVersionUID = 2847620533572167031L;
    private static final String name = "Phi index";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countScore(PairMatch pm) {
        double numerator = pm.tp * pm.tn - pm.fn * pm.fp;
        double denom = (pm.tp + pm.fn) * (pm.tp + pm.fp) * (pm.fn + pm.tn) * (pm.fp + pm.tn);
        return numerator / denom;
    }

}
