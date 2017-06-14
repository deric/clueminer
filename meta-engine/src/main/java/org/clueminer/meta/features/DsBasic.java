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
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.meta.api.DataStats;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Features commonly used for algorithm selection.
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = DataStats.class)
public class DsBasic<E extends Instance> implements DataStats<E> {

    public static final String LOG_SIZE = "log2size";
    public static final String LOG_ATTRS = "log2attrs";

    @Override
    public String[] provides() {
        return new String[]{LOG_SIZE, LOG_ATTRS};
    }

    @Override
    public double evaluate(Dataset<E> dataset, String feature, Props params) {
        switch (feature) {
            case LOG_SIZE:
                return logSize(dataset);
            case LOG_ATTRS:
                return logAttrs(dataset);
            default:
                throw new UnsupportedOperationException("unsupported feature: " + feature);
        }
    }

    @Override
    public void computeAll(Dataset<E> dataset, HashMap<String, Double> features, Props params) {
        features.put(LOG_SIZE, logSize(dataset));
        features.put(LOG_ATTRS, logAttrs(dataset));
    }

    public double logAttrs(Dataset<E> dataset) {
        return Math.log(dataset.attributeCount());
    }

    public double logSize(Dataset<E> dataset) {
        return Math.log(dataset.size());
    }

}
