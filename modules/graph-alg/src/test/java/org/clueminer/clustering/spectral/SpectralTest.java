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
package org.clueminer.clustering.spectral;

import java.util.AbstractQueue;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.aggl.Element;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class SpectralTest<E extends Instance> {

    private final Spectral subject;

    public SpectralTest() {
        subject = new Spectral();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testJainEpsGraphCluster() {
        Dataset<E> dataset = (Dataset<E>) FakeClustering.schoolData();
        Props params = new Props();
        params.putInt(SpectralClustering.K, 2);
        params.putDouble(SpectralClustering.SIGMA, 2);
        //params.put(SpectralClustering.EPS, 0.3);

        Clustering clustering = subject.cluster(dataset, params);
        System.out.println("clustering: " + clustering.fingerprint());

        assertEquals(dataset.size(), clustering.instancesCount());
    }

}
