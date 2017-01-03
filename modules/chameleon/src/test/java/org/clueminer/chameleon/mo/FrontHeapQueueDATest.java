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
package org.clueminer.chameleon.mo;

import java.util.ArrayList;
import java.util.HashSet;
import org.clueminer.chameleon.GraphCluster;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class FrontHeapQueueDATest<E extends Instance, C extends GraphCluster<E>, P extends MoPair<E, C>> extends AbstractQueueTest<E, C, P> {

    private FrontHeapQueueDA<E, C, P> queue;
    private PairMergerMOH<E, C, P> merger;

    @Test
    public void testIterator() {
        Props props = new Props();
        PairMergerMOH merger = initializeMerger((Dataset<E>) FakeDatasets.kumarData(), new PairMergerMOH<E, C, P>());
        ArrayList<P> pairs = merger.createPairs(merger.getClusters().size(), props);
        HashSet<Integer> blacklist = new HashSet<>();
        queue = new FrontHeapQueueDA(5, blacklist, merger.objectives, props);
        queue.addAll(pairs);

        int i = 0;
        for (P p : queue) {
            assertNotNull(p);
            i++;
        }
        assertEquals(i, queue.size());
    }

    @Test
    public void testIris() {
        Props props = new Props();
        merger = initializeMerger((Dataset<E>) FakeDatasets.irisDataset());
        ArrayList<P> pairs = merger.createPairs(merger.getClusters().size(), props);
        HashSet<Integer> blacklist = new HashSet<>();
        merger.queue = new FrontHeapQueueDA<>(5, blacklist, merger.objectives, props);
        merger.queue.addAll(pairs);

        //merge some items - just enough to overflow queue to buffer
        for (int i = 0; i < 5; i++) {
            merger.singleMerge(merger.queue.poll(), props, 0);
        }
        //make sure we iterate over all items
        int i = 0;
        for (Object p : merger.queue) {
            assertNotNull(p);
            i++;
        }
        assertEquals(i, merger.queue.size());
    }

}
