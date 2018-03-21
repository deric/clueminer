/*
 * Copyright (C) 2011-2018 clueminer.org
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
 * The coefficient of variation (CV) is defined as the ratio of the
 * standard deviation \sigma to the mean \mu
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = DataStats.class)
public class DsCV<E extends Instance> implements DataStats<E> {

    public static final String AVG_CV = "avg_cv";

    public double avgCv(Dataset<E> dataset) {
        double value = 0.0, cv;
        for (Attribute attr : dataset.attributeByRole(BasicAttrRole.INPUT)) {
            //std_dev should be divided by MEAN but in case of normalized data but
            //it causes double value overflow
            cv = attr.statistics(StatsNum.STD_DEV) / attr.statistics(StatsNum.MEDIAN);
            value += cv;
        }
        return value / dataset.attributeCount();
    }

    @Override
    public String[] provides() {
        return new String[]{AVG_CV};
    }

    @Override
    public double evaluate(Dataset<E> dataset, String feature, Props params) {
        switch (feature) {
            case AVG_CV:
                return avgCv(dataset);
            default:
                throw new UnsupportedOperationException("unsupported feature: " + feature);
        }
    }

    @Override
    public void computeAll(Dataset<E> dataset, HashMap<String, Double> features, Props params) {
        features.put(AVG_CV, avgCv(dataset));
    }

}
