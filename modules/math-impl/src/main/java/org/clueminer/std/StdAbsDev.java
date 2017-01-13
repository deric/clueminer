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
package org.clueminer.std;

import org.clueminer.math.Standardisation;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Standardisation.class)
public class StdAbsDev extends StdDev {

    public static final String name = "Standardised measurement";

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void computeDev(double[][] a, double[] avg, double[] dev, int m, int n) {
        int i, j;
        //square computeDev from average
        for (j = 0; j < n; j++) {
            for (i = 0; i < m; i++) {
                dev[j] += Math.abs(a[i][j] - avg[j]);
            }
            //according to some statisticians using (m-1) is more precise than just m
            dev[j] = Math.sqrt(dev[j] / (m - 1));
        }
    }
}
