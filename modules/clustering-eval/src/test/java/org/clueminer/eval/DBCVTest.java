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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

public class DBCVTest<E extends Instance, C extends Cluster<E>> {

    private final DBCV subject = new DBCV();
    private static final double DELTA = 1e-9;

    @Test
    public void testTrivial() throws Exception {
        Clustering<E, C> clusters = (Clustering<E, C>) Clusterings.newList();
        assertEquals(0.0, subject.score(clusters), DELTA);
    }

    @Test
    public void testSimple() throws Exception {
        Clustering<E, C> clusters = FakeClustering.kumar();
        subject.score(clusters);
    }

    @Ignore
    public void testSpirals() throws Exception {
        Clustering<E, C> clusters = FakeClustering.spirals();
        assertEquals(0.9478, subject.score(clusters), DELTA);
    }

}
