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
 * Standard deviation of all attribute's first quartiles divided by their means.
 *
 * @author deric
 */
@ServiceProvider(service = DataStats.class)
public class DsCVQ1<E extends Instance> implements DataStats<E> {

    @Override
    public String getName() {
        return "cvq1";
    }

    @Override
    public double evaluate(Dataset<E> dataset) {
        Attribute[] attrs = dataset.attributeByRole(BasicAttrRole.INPUT);
        double mean, value;
        double stdDev;
        double mOld = 0, mNew = 0, sOld = 0, sNew = 0;
        int i;
        for (i = 0; i < attrs.length; i++) {
            value = attrs[i].statistics(StatsNum.Q1);

            if (i == 0) {
                mOld = value;
                mNew = value;
                sOld = 0.0;
            } else {
                mNew = mOld + (value - mOld) / i;
                sNew = sOld + (value - mOld) * (value - mNew);

                // set up for next iteration
                mOld = mNew;
                sOld = sNew;
            }
        }
        mean = (i > 0) ? mNew : 0.0;
        stdDev = (i > 1) ? sNew / i : 0.0;
        return stdDev / mean;
    }

}
