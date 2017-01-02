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
 * Under the null hypothesis H_0 that the discordances between the clusterings
 * are random, the index follows approximatively a normal distribution. It is an
 * adaptation of the non-parametric test of McNemar for the comparison of
 * frequencies between two paired samples.
 *
 * @author deric
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class McNemar extends AbstractCountingPairs {

    private static final long serialVersionUID = 1647620533572167034L;
    private static final String name = "McNemar index";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countScore(PairMatch pm, Props params) {
        return (pm.tn - pm.fp) / Math.sqrt(pm.tn + pm.fp);
    }

}
