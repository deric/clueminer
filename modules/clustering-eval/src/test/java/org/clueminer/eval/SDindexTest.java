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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class SDindexTest {

    private final SDindex subject;
    private static final double DELTA = 1e-9;

    public SDindexTest() {
        subject = new SDindex(EuclideanDistance.getInstance());
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testScore() throws ScoreException {
        double scoreBetter = subject.score(FakeClustering.iris());
        double scoreWorser = subject.score(FakeClustering.irisWrong5());
        //TODO: on java 1.8.0_31 this score is: 3.706709966847651
        //assertEquals(3.7044979905303097, scoreBetter, delta);

        //should recognize "better" clustering (hand made clustering based on labels)
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
    }

    @Test
    public void testTransitivity() throws ScoreException {
        double a = subject.score(FakeClustering.irisTwoClusters());
        double b = subject.score(FakeClustering.iris());
        double c = subject.score(FakeClustering.irisWrong4());

        assertTrue(a + " > " + b, subject.isBetter(a, b));
        assertTrue(b + " > " + c, subject.isBetter(b, c));
        assertTrue(a + " > " + c, subject.isBetter(a, c));
    }

    @Test
    public void testCompareScore() throws ScoreException {
        double scoreBetter = subject.score(FakeClustering.iris());
        double scoreWorser = subject.score(FakeClustering.irisWrong2());
        //should recognize better clustering
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
    }

    @Test
    public void testIsMaximized() {
        assertEquals(false, subject.isMaximized());
    }

    /**
     * Check against definition (and tests in R package clusterCrit)
     * https://cran.r-project.org/web/packages/clusterCrit/index.html
     *
     * NOTE: There's a small problem with precision of floating point
     * operations. First 7 decimal digits seems to match.
     */
    @Test
    public void testClusterCrit() {
        double dis = subject.dispersion(FakeClustering.int100p4());
        //clusterCrit = 0.424825246347848
        //TODO: check dispersion computation
        assertEquals(0.424825246347848, dis, DELTA);

        double scat = subject.scattering(FakeClustering.int100p4());
        //clusterCrit = 0.0323239791483279
        //small difference is caused by variance being normalized by (n - 1) instead of just (n)
        assertEquals(0.03224316920045716, scat, DELTA);
    }

    @Test
    public void testScattering() throws ScoreException {
        double scatt = subject.scattering(FakeClustering.iris());
        assertEquals(0.10827421364989398, scatt, DELTA);
    }

    @Test
    public void testDispersion() throws ScoreException {
        double dis = subject.dispersion(FakeClustering.iris());
        assertEquals(1.43630554195769, dis, DELTA);

        Clustering<Instance, Cluster<Instance>> c = FakeClustering.iris();
        System.out.println("iris");
        for (Cluster clust : c) {
            System.out.println(clust.getCentroid().toString());
        }
    }

}
