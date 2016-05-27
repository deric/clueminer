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
 * The coefficient of variation (CV) is defined as the ratio of the
 * standard deviation \sigma to the mean \mu
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = DataStats.class)
public class DsCV<E extends Instance> implements DataStats<E> {

    @Override
    public String getName() {
        return "cv";
    }

    @Override
    public double evaluate(Dataset<E> dataset) {
        double value = 0.0, cv;
        for (Attribute attr : dataset.attributeByRole(BasicAttrRole.INPUT)) {
            //std_dev should be divided by MEAN but in case of normalized data
            //it causes double value overflow
            cv = attr.statistics(StatsNum.STD_DEV) / attr.statistics(StatsNum.MEDIAN);
            value += cv;
        }
        return value / (double) dataset.attributeCount();
    }

}
