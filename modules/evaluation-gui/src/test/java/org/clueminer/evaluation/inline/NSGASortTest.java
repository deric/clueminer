/*
 * Copyright (C) 2011-2015 clueminer.org
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
import org.clueminer.eval.DaviesBouldin;
import org.clueminer.eval.external.NMIsqrt;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class NSGASortTest {

    private NSGASort subject;

    public NSGASortTest() {
        subject = new NSGASort();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

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
        eval.add(new DaviesBouldin());

        subject.sort(clusterings, eval);

    }

}
