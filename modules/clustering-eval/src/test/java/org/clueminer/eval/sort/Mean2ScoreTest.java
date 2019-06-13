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
package org.clueminer.eval.sort;

import java.util.LinkedList;
import java.util.List;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.eval.AIC;
import org.clueminer.eval.CIndex;
import org.clueminer.eval.CalinskiHarabasz;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class Mean2ScoreTest {

    private static final Mean2Score subject = new Mean2Score();

    private static final double DELTA = 1e-9;

    @Test
    public void testSort() {
        Clustering[] clusterings = new Clustering[6];
        int i = 0;

        clusterings[i++] = FakeClustering.iris();
        clusterings[i++] = FakeClustering.irisMostlyWrong();
        clusterings[i++] = FakeClustering.irisWrong4();
        clusterings[i++] = FakeClustering.irisWrong();
        clusterings[i++] = FakeClustering.irisWrong2();
        clusterings[i++] = FakeClustering.irisWrong5();

        List<ClusterEvaluation> eval = new LinkedList<>();
        eval.add(new CalinskiHarabasz());
        eval.add(new AIC());
        eval.add(new CIndex());

        Clustering[] res = subject.sort(clusterings, eval);
        assertEquals(clusterings.length, res.length);
        //we use supervised criterion, first solution must be "correct" clustering

        Mean2Comparator comp = subject.getComparator();
        double value;
        for (int j = 0; j < res.length; j++) {
            value = comp.aggregatedScore(res[j]);
            assertTrue("given two objectives, the value can't be worser", value <= 10 * eval.size());
            System.out.println(j + ": " + res[j].fingerprint() + " = " + comp.aggregatedScore(res[j]));
        }

        Clustering best = res[res.length - 1];
        assertEquals(FakeClustering.iris(), best);
        // in this case it's 2.0, but it doesn't have to be
        assertEquals(comp.getMin(), comp.aggregatedScore(best), DELTA);
    }

}
