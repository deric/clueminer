/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.rank;

import java.util.HashMap;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class SpearmanTest {

    private static final Spearman subject = new Spearman();
    private static final double DELTA = 1e-9;

    @Test
    public void testCorrelation() {
        //ClusteringComparator comp = new ClusteringComparator(new NMIsqrt());
        Clustering a = FakeClustering.iris();
        a.setId(0);
        Clustering b = FakeClustering.irisWrong2();
        b.setId(1);
        Clustering c = FakeClustering.irisWrong5();
        c.setId(2);

        Clustering[] c1 = new Clustering[]{a, b, c};
        Clustering[] c2 = new Clustering[]{c, a, b};
        Clustering[] c3 = new Clustering[]{a, c, b};
        Clustering[] c4 = new Clustering[]{c, b, a};
        HashMap<Integer, Integer> map = new HashMap<>();
        //Arrays.sort(c1, comp);

        //the same
        assertEquals(1.0, subject.correlation(c1, c1, map), DELTA);
        assertEquals(-0.5, subject.correlation(c1, c2, map), DELTA);
        assertEquals(0.5, subject.correlation(c1, c3, map), DELTA);
        //completely opposite ranking
        assertEquals(-1.0, subject.correlation(c1, c4, map), DELTA);

    }

}
