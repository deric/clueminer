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
package org.clueminer.meta.ranking;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.AIC;
import org.clueminer.eval.PointBiserial;
import org.clueminer.eval.RatkowskyLance;
import org.clueminer.eval.external.VMeasure;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ParetoFrontQueueTest {

    private static ParetoFrontQueue subject;

    @Before
    public void setUpClass() {
        List<ClusterEvaluation<Instance, Cluster<Instance>>> objectives = new LinkedList<>();
        objectives.add(new AIC<>());
        objectives.add(new RatkowskyLance<>());
        subject = new ParetoFrontQueue(5, new HashSet<Integer>(), objectives, new PointBiserial());

        subject.add(FakeClustering.iris());
        subject.add(FakeClustering.irisMostlyWrong());
        subject.add(FakeClustering.irisWrong());
        subject.add(FakeClustering.irisWrong2());
        subject.add(FakeClustering.irisWrong4());
    }

    @Test
    public void testPoll() {
        Clustering c = subject.poll();
        assertNotNull(c);
        assertEquals(3, c.size());
        assertEquals(true, subject.hasNext());

        assertEquals(false, subject.isEmpty());
    }

    @Test
    public void testSize() {
        System.out.println(subject.stats());
        assertEquals(5, subject.size());
        //System.out.println(subject.toString());
        subject.printRanking(new VMeasure());
    }

    @Test
    public void testNumFronts() {
        //three non-empty fronts
        //TODO: why 2?
        assertEquals(2, subject.numFronts());
    }

}
