/*
 * Copyright (C) 2011-2019 clueminer.org
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

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class NMIARITest extends ExternalTest {

    private static Clustering irisCorrect;
    private static Clustering irisWrong;

    public NMIARITest() throws FileNotFoundException, IOException {
        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong2();
        subject = new NMIARI();
    }

    /**
     * Test of score method, of class NMI.
     */
    @Test
    public void testScore_Clustering_Clustering() throws ScoreException {
        //this is fixed clustering which correspods to true classes in dataset
        measure(FakeClustering.iris(), FakeClustering.iris(), 1.0);

        double score = measure(irisWrong, irisCorrect, 0.6496820278112178);

        double score2 = measure(FakeClustering.irisWrong(), irisCorrect, 0.06793702240876041);
        assertTrue(score2 < score);
    }

    /**
     * Test of score method, of class NMI.
     */
    @Test
    public void testScore_Clustering_Dataset() throws ScoreException {
        measure(FakeClustering.iris(), 1.0);

        double score = measure(irisWrong, 0.5496416196123189);
        double score2 = measure(FakeClustering.irisWrong(), irisCorrect, 0.06793702240876041);

        assertTrue(score2 < score);
    }

    /**
     * Test of isBetter method, of class NMI.
     */
    @Test
    public void testCompareScore() {
        //one is better than zero
        assertTrue(subject.isBetter(1.0, 0.0));
        assertTrue(subject.isBetter(1.0, 0.5));
        assertTrue(subject.isBetter(1.0, 0.9999));
    }
}
