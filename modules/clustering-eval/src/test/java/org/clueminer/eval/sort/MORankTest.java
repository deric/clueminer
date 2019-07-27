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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.AIC;
import org.clueminer.eval.CalinskiHarabasz;
import org.clueminer.eval.RatkowskyLance;
import org.clueminer.eval.ScottSymons;
import org.clueminer.eval.external.NMImax;
import org.clueminer.eval.external.NMIsqrt;
import org.clueminer.eval.external.NMIsum;
import static org.clueminer.eval.sort.MORank.PROP_RANK;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MORankTest<E extends Instance, C extends Cluster<E>> {

    private static MORank subject = new MORank();

    private static final double DELTA = 1e-9;

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
        eval.add(new RatkowskyLance());

        Clustering[] res = subject.sort(clusterings, eval);
        assertEquals(clusterings.length, res.length);
        //we use supervised criterion, first solution must be "correct" clustering
        assertEquals(FakeClustering.iris(), clusterings[0]);
    }

    @Test
    public void testIris() throws ScoreException {
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
        eval.add(new ScottSymons());

        Clustering[] res = subject.sort(clusterings, eval);
        assertEquals(clusterings.length, res.length);
        //we use supervised criterion, first solution must be "correct" clustering

        Clustering best = res[res.length - 1];
        ClusterEvaluation ext = new NMIsqrt();

        assertEquals(1.0, ext.score(best), DELTA);
    }

    /**
     * Having ideal objectives we should be able to rank clusterings as expected
     *
     * @throws ScoreException
     */
    @Test
    public void testRankings() throws ScoreException {
        Clustering[] clusterings = new Clustering[7];
        int i = 0;

        clusterings[i++] = FakeClustering.iris();
        clusterings[i++] = FakeClustering.irisMostlyWrong();
        clusterings[i++] = FakeClustering.irisWrong4();
        clusterings[i++] = FakeClustering.irisWrong();
        clusterings[i++] = FakeClustering.irisWrong2();
        clusterings[i++] = FakeClustering.irisWrong5();
        clusterings[i++] = FakeClustering.irisTwoClusters();

        List<ClusterEvaluation> eval = new LinkedList<>();
        eval.add(new NMImax());
        eval.add(new NMIsqrt());
        List<ArrayList<Clustering<E, C>>> fronts = subject.computeRankings(clusterings, eval, new NMIsum());
        subject.printFronts(fronts);

        eval.add(new NMIsum());
        Clustering[] res = subject.sort(clusterings, eval);
        subject.printFlatten(res);
        ClusterEvaluation ext = new NMIsqrt();

        //starts from worst clustering found
        Clustering prev = res[0];
        for (int j = 1; j < res.length; j++) {
            assertTrue("expects " + prev.fingerprint() + " to have better score than " + res[j].fingerprint(),
                    ext.compare(prev, res[j]) < 0);
            assertTrue("expects " + prev.fingerprint() + " to have better rank than " + res[j].fingerprint(),
                    prev.getParams().getInt(PROP_RANK) > res[j].getParams().getInt(PROP_RANK));
            prev = res[j];
        }
        assertEquals(clusterings.length, res.length);
        //we use supervised criterion, last solution must be "correct" clustering
        Clustering best = res[res.length - 1];

        assertEquals(1.0, ext.score(best), DELTA);
    }

}
