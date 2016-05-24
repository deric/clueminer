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
 * Sometimes called "Gamma", which could be misleading due to internal measure
 * with the same name.
 *
 * @author deric
 *
 * HUBERT, L. J., and ARABIE, P. (1985), “Comparing Partitions”, Journal of
 * Classification, 2, 193-218.
 *
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class Hubert extends AbstractCountingPairs {

    private static final long serialVersionUID = 101045082257039885L;
    private static final String name = "Hubert";

    @Override
    public String getName() {
        return name;
    }

    /**
     * Standardized variant (centered and reduced) of the Russel-Rao index
     *
     * @param pm
     * @return Hubert index
     */
    @Override
    public double countScore(PairMatch pm, Props params) {
        double num = pm.sum() * pm.tp - (pm.tp + pm.fn) * (pm.tp + pm.fp);
        double denom = (pm.tp + pm.fn) * (pm.tp + pm.fp);
        return num / Math.sqrt(denom * (pm.tn + pm.fp) * (pm.tn + pm.fn));
    }

}
