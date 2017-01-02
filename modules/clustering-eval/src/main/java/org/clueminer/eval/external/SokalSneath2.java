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
 *
 * @author deric
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class SokalSneath2 extends AbstractCountingPairs {

    private static final long serialVersionUID = 7647620533572167035L;
    private static final String NAME = "Sokal-Sneath-2";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double countScore(PairMatch pm, Props params) {
        double num = pm.tp + pm.tn;
        double den = num + (pm.fn + pm.fp) / 2.0;

        return num / den;
    }

}
