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
 * Fowlkes-Mallows coefficient
 *
 * A Method for Comparing Two Hierarchical Clusterings - E. B. Fowlkes, C. L.
 * Mallows Journal of the American Statistical Association Vol. 78, Iss. 383,
 * 1983
 *
 * @see http://en.wikipedia.org/wiki/Fowlkes%E2%80%93Mallows_index
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class FowlkesMallows extends AbstractCountingPairs {

    private static final long serialVersionUID = 101045082257039885L;
    private static final String name = "Fowlkes-Mallows";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countScore(PairMatch pm, Props params) {
        return pm.tp / Math.sqrt((pm.tp + pm.fp) * (pm.tp + pm.fn));
    }

}
