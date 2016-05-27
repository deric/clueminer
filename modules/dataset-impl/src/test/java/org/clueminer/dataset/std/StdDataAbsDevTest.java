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

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class StdDataAbsDevTest {

    private static final double[][] data = new double[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}};
    private static final double[][] data2 = new double[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, -10}, {-5, -2, 19, 1, 5}};
    private static final double delta = 1e-9;
    private static final StdDataAbsDev subject = new StdDataAbsDev();
    private static final String method = StdDataAbsDev.name;

    @Test
    public void testOptimizeData() {
        Matrix res = Scaler.standartize(data, method, false);

        Dataset<? extends Instance> dataset = new ArrayDataset(data);
        Dataset<? extends Instance> out = subject.optimize(dataset);

        assertEquals(dataset, out.getParent());

        for (int i = 0; i < dataset.size(); i++) {
            for (int j = 0; j < dataset.attributeCount(); j++) {
                assertEquals(res.get(i, j), out.get(i, j), delta);
            }
        }
    }

    @Test
    public void testOptimizeData2() {
        Matrix res = Scaler.standartize(data2, method, false);

        Dataset<? extends Instance> dataset = new ArrayDataset(data2);
        Dataset<? extends Instance> out = subject.optimize(dataset);

        for (int i = 0; i < dataset.size(); i++) {
            for (int j = 0; j < dataset.attributeCount(); j++) {
                assertEquals(res.get(i, j), out.get(i, j), delta);
            }
        }
    }

}
