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
import org.clueminer.dataset.api.StatsNum;
import org.clueminer.meta.api.DataStats;
import org.openide.util.lookup.ServiceProvider;

/**
 * The Pearson median skewness, or second skewness coefficient
 *
 * @author deric
 */
@ServiceProvider(service = DataStats.class)
public class DsMedianSkewness<E extends Instance> implements DataStats<E> {

    @Override
    public String getName() {
        return "MedianSkew";
    }

    @Override
    public double evaluate(Dataset<E> dataset) {
        double avg = 0.0, value;
        for (Attribute attr : dataset.attributeByRole(BasicAttrRole.INPUT)) {
            value = 3 * (attr.statistics(StatsNum.AVG) - attr.statistics(StatsNum.MEDIAN)) / attr.statistics(StatsNum.STD_DEV);
            avg += value;
        }
        return avg / dataset.attributeCount();
    }

}
