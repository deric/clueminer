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
package org.clueminer.evaluation.inline;

import java.util.LinkedList;
import java.util.List;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.eval.AIC;
import org.clueminer.eval.external.NMIsqrt;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class NSGASortTest {


    @Test
    public void testSort() {
        Clustering[] clusterings = new Clustering[6];
        int i = 0;
        //evalPlot.setGolden(FakeClustering.iris());
        clusterings[i++] = FakeClustering.iris();
        clusterings[i++] = FakeClustering.irisMostlyWrong();
        clusterings[i++] = FakeClustering.irisWrong4();
        clusterings[i++] = FakeClustering.irisWrong();
        clusterings[i++] = FakeClustering.irisWrong2();
        clusterings[i++] = FakeClustering.irisWrong5();

        List<ClusterEvaluation> eval = new LinkedList<>();
        eval.add(new NMIsqrt());
        eval.add(new AIC());

        Clustering[] res = NSGASort.sort(clusterings, eval);
        assertEquals(clusterings.length, res.length);
        //we use supervised criterion, first solution must be "correct" clustering
        assertEquals(FakeClustering.iris(), clusterings[0]);
    }

}
