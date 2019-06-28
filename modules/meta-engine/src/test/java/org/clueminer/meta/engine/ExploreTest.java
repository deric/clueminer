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
package org.clueminer.meta.engine;

import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.api.Individual;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.report.MemInfo;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class ExploreTest<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>> {

    private final Explore<I, E, C> subject;
    private MemInfo mem;

    public ExploreTest() {
        subject = new Explore();
    }

    @Before
    public void setUp() {
        //report = new ConsoleReporter();
        //subject.addEvolutionListener(report);
        mem = new MemInfo();
    }

    @Test
    public void testIris() throws Exception {
        subject.setDataset((Dataset<E>) FakeDatasets.schoolData());
        subject.setMaxSolutions(15);
        subject.setTimePerTask(100L);

        mem.startClock();
        List<Clustering<E, C>> res = subject.call();
        assertNotNull(res);
        //assertEquals(15, res.size());

        mem.report();
    }

}
