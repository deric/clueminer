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
package org.clueminer.eval.external;

import com.google.common.collect.Table;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.ScoreException;
import static org.clueminer.eval.external.ExternalTest.delta;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.eval.utils.PairMatch;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author tombart
 */
public class PrecisionTest extends ExternalTest {

    public PrecisionTest() throws FileNotFoundException, IOException {
        subject = new Precision();
    }

    @Test
    public void testScore_Clustering_Dataset() throws ScoreException {
        //this is fixed clustering which correspods to true classes in dataset
        measure(FakeClustering.iris(), 1.0);
    }

    /**
     * Test of isBetter method, of class Precision.
     */
    @Test
    public void testCompareScore() {
        //first one should be better
        assertEquals(true, subject.isBetter(1.0, 0.4));
    }

    @Test
    public void testOneClassPerCluster() throws ScoreException {
        assertEquals(0.0, subject.score(oneClassPerCluster()), delta);
    }

    @Test
    public void testScore_Clustering_Clustering() throws ScoreException {
        double score;
        measure(FakeClustering.iris(), 1.0);

        measure(FakeClustering.irisWrong4(), FakeClustering.iris(), 0.8367346938775511);
        measure(FakeClustering.irisWrong5(), FakeClustering.iris(), 0.9346938775510204);

        //each cluster should have this scores:
        //Cabernet = 0.6923
        //Syrah = 0.5555
        //Pinot = 0.8000
        score = measure(FakeClustering.wineClustering(), FakeClustering.wineCorrect(), 0.46774193548387094);

        //when using class labels, result should be the same
        measure(FakeClustering.wineClustering(), score);
    }

    @Test
    public void testCountScore() throws ScoreException {
        double score = subject.score(FakeClustering.irisMostlyWrong());
        System.out.println("mostly wrong precision  = " + score);
        Table<String, String, Integer> table = CountingPairs.contingencyTable(FakeClustering.irisMostlyWrong());
        CountingPairs.dumpTable(table);

        Precision precision = (Precision) subject;
        PairMatch pm = CountingPairs.getInstance().matchPairs(FakeClustering.irisMostlyWrong());
        pm.dump();
        assertEquals(0.9866666666666667, precision.countScore(pm, null), delta);
    }

    /**
     * Check against definition (and tests in R package clusterCrit)
     * https://cran.r-project.org/web/packages/clusterCrit/index.html
     *
     * NOTE: There's a small problem with precision of floating point
     * operations. First 7 decimal digits seems to match.
     */
    @Test
    public void testClusterCrit() throws ScoreException {
        measure(ext100p2, ext100p3, 0.511811017990112);
    }
}
