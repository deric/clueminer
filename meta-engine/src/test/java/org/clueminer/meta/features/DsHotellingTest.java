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

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import static org.clueminer.meta.features.DsBaseTest.stat;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DsHotellingTest extends DsBaseTest {

    @BeforeClass
    public static void setUpClass() {
        stat = new DsHotelling();
    }

    public void testEvaluate() {
//        double v = stat.evaluate(FakeDatasets.irisDataset(), DsGraph.EDGES, null);
        //assertEquals(0.31999051830383357, v, DELTA);
    }

    @Test
    public void testCovariance() {
        double data[][] = new double[][]{
            {90, 60, 90},
            {90, 90, 30},
            {60, 60, 60},
            {60, 60, 90},
            {30, 30, 30}
        };
        Dataset<? extends Instance> dataset = new ArrayDataset(data);
        DsHotelling hot = (DsHotelling) stat;
        double cov = hot.covariance(dataset);
        System.out.println("cov = " + cov);
    }

    @Test
    public void testCov() {
        RealMatrix mx = MatrixUtils.createRealMatrix(new double[][]{
            {90, 60, 90},
            {90, 90, 30},
            {60, 60, 60},
            {60, 60, 90},
            {30, 30, 30}
        });
        RealMatrix cov = new Covariance(mx).getCovarianceMatrix();
        System.out.println("cov: " + cov.toString());
    }
}
