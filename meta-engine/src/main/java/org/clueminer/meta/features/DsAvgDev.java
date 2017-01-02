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
package org.clueminer.meta.features;

import java.util.HashMap;
import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.StatsNum;
import org.clueminer.meta.api.DataStats;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Average variance over all attributes
 *
 * @author deric
 */
@ServiceProvider(service = DataStats.class)
public class DsAvgDev<E extends Instance> implements DataStats<E> {

    public static final String AVG_DEV = "avg_dev";

    public double avgDev(Dataset<E> dataset) {
        double value = 0.0;
        for (Attribute attr : dataset.attributeByRole(BasicAttrRole.INPUT)) {
            value += attr.statistics(StatsNum.VARIANCE);
        }
        return value / dataset.attributeCount();
    }

    @Override
    public String[] provides() {
        return new String[]{AVG_DEV};
    }

    @Override
    public double evaluate(Dataset<E> dataset, String feature, Props params) {
        switch (feature) {
            case AVG_DEV:
                return avgDev(dataset);
            default:
                throw new UnsupportedOperationException("unsupported feature: " + feature);
        }
    }

    @Override
    public void computeAll(Dataset<E> dataset, HashMap<String, Double> features, Props params) {
        features.put(AVG_DEV, avgDev(dataset));
    }

}
