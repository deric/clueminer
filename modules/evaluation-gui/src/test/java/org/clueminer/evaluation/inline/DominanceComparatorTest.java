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
package org.clueminer.evaluation.inline;

import java.util.LinkedList;
import java.util.List;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.eval.AIC;
import org.clueminer.eval.BIC;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DominanceComparatorTest {

    private DominanceComparator subject;

    public DominanceComparatorTest() {
        List<ClusterEvaluation> objectives = new LinkedList<>();
        objectives.add(new AIC());
        objectives.add(new BIC());
        subject = new DominanceComparator(objectives);
    }

    @Test
    public void testCompare() {
        int res = subject.compare(FakeClustering.iris(), FakeClustering.iris());
        //identity - must be same
        assertEquals(0, res);

        res = subject.compare(FakeClustering.iris(), FakeClustering.irisMostlyWrong());
        // first one is better
        assertEquals(-1, res);

        res = subject.compare(FakeClustering.irisMostlyWrong(), FakeClustering.iris());
        // second one is better
        assertEquals(1, res);
    }

}
