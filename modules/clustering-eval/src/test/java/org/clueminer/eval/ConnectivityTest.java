/*
 * Copyright (C) 2015 clueminer.org
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

import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ConnectivityTest {

    private final Connectivity subject;

    public ConnectivityTest() {
        subject = new Connectivity();
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    @Test
    public void testIris() {
        double scoreBetter = subject.score(FakeClustering.iris(), FakeDatasets.irisDataset());
        double scoreWorser = subject.score(FakeClustering.irisMostlyWrong(), FakeDatasets.irisDataset());

        System.out.println("better: " + scoreBetter);
        System.out.println("worser: " + scoreWorser);

        //should recognize better clustering
        //assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
    }

    @Test
    public void testScore_Clustering_Dataset() {

    }

    @Test
    public void testScore_3args() {
    }

    @Test
    public void testIsBetter() {
    }

    @Test
    public void testIsMaximized() {
        assertEquals(false, subject.isMaximized());
    }

}
