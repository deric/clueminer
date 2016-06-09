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
package org.clueminer.meta.engine;

import java.util.SortedMap;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.external.NMIsum;
import org.clueminer.evolution.api.Individual;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.meta.ranking.ParetoFrontQueue;
import org.clueminer.report.MemInfo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 * @param <I>
 * @param <E>
 * @param <C>
 */
public class MetaSearchTest<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>> {

    private final MetaSearch<I, E, C> subject;
    private MemInfo mem;

    public MetaSearchTest() {
        subject = new MetaSearch();
    }

    @Before
    public void setUp() {
        //report = new ConsoleReporter();
        //subject.addEvolutionListener(report);
        mem = new MemInfo();
    }

    @Test
    public void testIris() throws Exception {
        subject.setDataset((Dataset<E>) FakeDatasets.irisDataset());
        subject.setGenerations(1);
        subject.setPopulationSize(5);

        mem.startClock();
        ParetoFrontQueue<E, C, Clustering<E, C>> q = subject.call();
        SortedMap<Double, Clustering<E, C>> ranking = q.computeRanking();
        assertNotNull(ranking);
        //there should be always 0.0 key (best solution)
        assertTrue(ranking.containsKey(0.0));
        q.printRanking(new NMIsum());

        mem.report();
    }

    //@Test
    public void testVehicle() {
        subject.setDataset((Dataset<E>) FakeDatasets.vehicleDataset());
        subject.setGenerations(1);
        subject.setPopulationSize(5);

        mem.startClock();
        //TODO: make sure evolution works
        subject.run();
        mem.report();
    }

}
