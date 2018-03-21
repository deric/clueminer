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
package org.clueminer.rank;

import java.util.HashMap;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Rank;
import org.openide.util.lookup.ServiceProvider;

/**
 * Standard correlation coefficient
 *
 * @author deric
 */
@ServiceProvider(service = Rank.class)
public class Correlation implements Rank {

    private static final String name = "correlation";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double correlation(Clustering[] ref, Clustering[] curr, HashMap<Integer, Integer> map) {
        int size = curr.length;

        if (size <= 1) {
            return 1.0;
        }

        double y11 = 0.0, y22 = 0.0, y12 = 0.0, c;

        //average value of a serie of int numbers (same for both external and internal)
        //sum of a serie divided by its number of members
        double avg = ((size * (0 + size)) >>> 1) / (size + 1);
        double diffX, diffY;
        for (int i = 0; i < size; i++) {
            diffX = curr[i].getId() - avg;
            diffY = ref[i].getId() - avg;
            y11 += Math.pow(diffX, 2);
            y22 += Math.pow(diffY, 2);
            y12 += diffX * diffY;
        }
        if (y11 * y22 == 0.0) {
            c = 1.0;
        } else {
            c = y12 / Math.sqrt(Math.abs(y11 * y22));
        }
        return c;
    }

}
