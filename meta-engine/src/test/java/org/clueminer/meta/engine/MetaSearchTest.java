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

import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.report.MemInfo;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MetaSearchTest {

    private MetaSearch subject;
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
    public void testIris() {
        subject.setDataset(FakeDatasets.irisDataset());
        subject.setGenerations(1);
        subject.setPopulationSize(5);

        mem.startClock();
        //TODO: make sure evolution works
        subject.run();
        mem.report();
    }

    @Test
    public void testVehicle() {
        subject.setDataset(FakeDatasets.vehicleDataset());
        subject.setGenerations(1);
        subject.setPopulationSize(5);

        mem.startClock();
        //TODO: make sure evolution works
        subject.run();
        mem.report();
    }

}
