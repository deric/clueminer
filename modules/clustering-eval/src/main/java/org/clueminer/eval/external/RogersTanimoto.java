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
package org.clueminer.eval.external;

import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.utils.PairMatch;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Rodgers-Tanimoto index
 *
 * @see
 * https://reference.wolfram.com/language/ref/RogersTanimotoDissimilarity.html
 * @author deric
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class RogersTanimoto extends AbstractCountingPairs {

    private static final long serialVersionUID = 6547620533572167031L;
    private static final String name = "Rogers-Tanimoto";

    @Override
    public String getName() {
        return name;
    }

    /**
     *
     *
     * @param pm
     * @return
     */
    @Override
    public double countScore(PairMatch pm, Props params) {
        double num = pm.fn + pm.fp;
        double den = pm.tp + 2 * (pm.fn + pm.fp) + pm.tn;

        return num / den;
    }

}
