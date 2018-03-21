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
package org.clueminer.dataset.std;

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.DataStandardization;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.StatsNum;
import org.clueminer.std.StdScale;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
@ServiceProvider(service = DataStandardization.class)
public class StdMinMax<E extends Instance> extends StdScale implements DataStandardization<E> {

    private static final Logger LOG = LoggerFactory.getLogger(DataScaler.class);

    @Override
    public Dataset<E> optimize(Dataset<E> dataset) {
        double min, max;
        Dataset<E> opt = (Dataset<E>) dataset.duplicate();
        Instance orig;
        Attribute attr;

        for (int j = 0; j < dataset.attributeCount(); j++) {
            attr = dataset.getAttribute(j);
            min = attr.statistics(StatsNum.MIN);
            max = attr.statistics(StatsNum.MAX);
            for (int i = 0; i < dataset.size(); i++) {
                opt.set(i, j, scaleToRange(dataset.get(i, j), min, max, getTargetMin(), getTargetMax()));
                if (j == 0) {
                    orig = dataset.get(i);
                    opt.get(i).setClassValue(orig.classValue());
                    opt.get(i).setId(orig.getId());
                    opt.get(i).setName(orig.getName());
                    opt.get(i).setAncestor(orig);
                    opt.changedClass(i, orig.classValue(), null);
                }
            }
        }

        return opt;
    }

}
