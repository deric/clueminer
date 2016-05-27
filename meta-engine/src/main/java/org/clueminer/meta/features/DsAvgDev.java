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
package org.clueminer.meta.features;

import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.meta.api.DataStats;
import org.clueminer.dataset.api.StatsNum;
import org.openide.util.lookup.ServiceProvider;

/**
 * Average variance over all attributes
 *
 * @author deric
 */
@ServiceProvider(service = DataStats.class)
public class DsAvgDev<E extends Instance> implements DataStats<E> {

    @Override
    public String getName() {
        return "avgDev";
    }

    @Override
    public double evaluate(Dataset<E> dataset) {
        double value = 0.0;
        for (Attribute attr : dataset.attributeByRole(BasicAttrRole.INPUT)) {
            value += attr.statistics(StatsNum.VARIANCE);
        }
        return value / dataset.attributeCount();
    }

}
