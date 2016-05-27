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

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.clueminer.meta.features.DsBaseTest.stat;
import org.clueminer.stats.AttrNumStats;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DsQCVTest extends DsBaseTest {

    @BeforeClass
    public static void setUpClass() {
        stat = new DsQCV();
    }

    @Test
    public void testEvaluate() {
        double v = stat.evaluate(FakeDatasets.irisDataset());
        assertEquals(0.35792111634070767, v, DELTA);
    }

    /**
     * Results based on R summary() function for Iris dataset
     * > data(iris)
     * > head(iris)
     * Sepal.Length Sepal.Width Petal.Length Petal.Width Species
     * 1 5.1 3.5 1.4 0.2 setosa
     * 2 4.9 3.0 1.4 0.2 setosa
     * 3 4.7 3.2 1.3 0.2 setosa
     * 4 4.6 3.1 1.5 0.2 setosa
     * 5 5.0 3.6 1.4 0.2 setosa
     * 6 5.4 3.9 1.7 0.4 setosa
     * > summary(iris)
     * Sepal.Length Sepal.Width Petal.Length Petal.Width
     * Min. :4.300 Min. :2.000 Min. :1.000 Min. :0.100
     * 1st Qu.:5.100 1st Qu.:2.800 1st Qu.:1.600 1st Qu.:0.300
     * Median :5.800 Median :3.000 Median :4.350 Median :1.300
     * Mean :5.843 Mean :3.057 Mean :3.758 Mean :1.199
     * 3rd Qu.:6.400 3rd Qu.:3.300 3rd Qu.:5.100 3rd Qu.:1.800
     * Max. :7.900 Max. :4.400 Max. :6.900 Max. :2.500
     * Species
     * setosa :50
     * versicolor:50
     * virginica :50 *
     */
    @Test
    public void testStats() {
        Dataset<? extends Instance> d = FakeDatasets.irisDataset();
        Attribute attr = d.getAttribute(0);
        assertEquals(4.3, attr.statistics(AttrNumStats.MIN), DELTA);
        assertEquals(5.1, attr.statistics(AttrNumStats.Q1), DELTA);
        assertEquals(5.8, attr.statistics(AttrNumStats.MEDIAN), DELTA);
        assertEquals(5.8433333333, attr.statistics(AttrNumStats.AVG), DELTA);
        assertEquals(6.4, attr.statistics(AttrNumStats.Q3), DELTA);
        assertEquals(7.9, attr.statistics(AttrNumStats.MAX), DELTA);
    }

}
