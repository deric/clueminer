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
package org.clueminer.dataset.std;

import org.clueminer.dataset.api.DataStandardization;
import org.clueminer.dataset.api.DataStandardizationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.std.StdNone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Normalize input data with given method.
 *
 * @author Tomas Barton
 * @param <E>
 */
public class DataScaler<E extends Instance> {

    private static final Logger LOG = LoggerFactory.getLogger(DataScaler.class);

    public Dataset<E> standartize(Dataset<E> dataset, String method, boolean logScale) {
        DataStandardizationFactory sf = DataStandardizationFactory.getInstance();
        Dataset<E> res;
        if (method.equals(StdNone.name)) {
            //nothing to optimize
            res = dataset;
        } else {
            DataStandardization std = sf.getProvider(method);
            if (std == null) {
                throw new RuntimeException("Standartization method " + std + " was not found");
            }
            LOG.info("scaling dataset {} name: {}", dataset.getClass().getName(), dataset.getName());
            res = std.optimize(dataset);
        }
        if (logScale) {
            StdMinMax scale = new StdMinMax();
            scale.setTargetMin(1);
            double max = res.max();
            double min = res.min();
            scale.setTargetMax(-min + max + 1);
            //normalize values, so that we can apply logarithm
            res = scale.optimize(res);
            // min-max normalized dataset is just intermediate step (shifted
            // so that values are bigger than 1.0), we'll set as parent
            // the dataset from previous step (the intermediate is never used)
            res.setParent(dataset);

            for (int i = 0; i < res.size(); i++) {
                for (int j = 0; j < res.attributeCount(); j++) {
                    res.set(i, j, Math.log(res.get(i, j)));
                }
            }
        }
        return res;
    }

}
