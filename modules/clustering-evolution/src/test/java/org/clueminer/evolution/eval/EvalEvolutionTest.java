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
package org.clueminer.evolution.eval;

import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.CalinskiHarabasz;
import org.clueminer.eval.external.Precision;
import org.clueminer.evolution.multim.ConsoleReporter;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.report.MemInfo;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class EvalEvolutionTest {

    protected EvalEvolution subject;
    protected ConsoleReporter report;
    protected MemInfo mem;

    @Before
    public void setUp() {
        subject = new EvalEvolution(new ClusteringExecutorCached());
        report = new ConsoleReporter();
        subject.addEvolutionListener(report);
        mem = new MemInfo();
    }

    @Test
    public void testRun() {
        subject.setDataset(FakeDatasets.irisDataset());
        subject.setGenerations(1);
        subject.setPopulationSize(5);
        //subject.setAlgorithm(new ));
        subject.setEvaluator(new CalinskiHarabasz());
        Props params = new Props();
        params.put(PropType.PERFORMANCE, AgglParams.KEEP_PROXIMITY, true);
        subject.setDefaultProps(params);
        ExternalEvaluator ext = new Precision();
        subject.setExternal(ext);

        mem.startClock();
        //TODO: make sure evolution works
        subject.run();
        mem.report();
    }
}
