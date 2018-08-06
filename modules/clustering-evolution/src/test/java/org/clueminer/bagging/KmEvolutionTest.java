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
package org.clueminer.bagging;

import org.clueminer.exec.ClusteringExecutorCached;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.AIC;
import org.clueminer.eval.CalinskiHarabasz;
import org.clueminer.eval.external.Precision;
import org.clueminer.evolution.multim.ConsoleReporter;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.report.MemInfo;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class KmEvolutionTest {

    protected KmEvolution subject;
    protected ConsoleReporter report;
    protected MemInfo mem;

    public KmEvolutionTest() {
    }

    @Before
    public void setUp() {
        Executor exec = new ClusteringExecutorCached(new KMeans());
        subject = new KmEvolution(exec);
        report = new ConsoleReporter();
        subject.addEvolutionListener(report);
        mem = new MemInfo();
    }

    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    /**
     * TODO: implement all operators
     */
    @Test
    public void testRun() {
        subject.setDataset(FakeDatasets.irisDataset());
        subject.setGenerations(1);
        subject.setPopulationSize(5);
        //subject.setAlgorithm(new ));
        subject.addObjective(new CalinskiHarabasz());
        subject.addObjective(new AIC());
        Props props = new Props();
        props.put("k", 3);
        props.put("max_k", 20);
        subject.setDefaultProps(props);
        ExternalEvaluator ext = new Precision();
        subject.setExternal(ext);

        mem.startClock();
        //TODO: make sure evolution works
        subject.run();
        mem.report();
    }

}
