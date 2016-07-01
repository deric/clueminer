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
package org.clueminer.chameleon.mo;

import java.util.ArrayList;
import java.util.HashSet;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class FrontHeapQueueMshTest<E extends Instance, C extends Cluster<E>, P extends MoPair<E, C>> extends AbstractQueueTest<E, C, P> {

    private FrontHeapQueueMsh<E, C, P> queue;

    @Test
    public void testIterator() {
        Props props = new Props();
        PairMergerMOH merger = initializeMerger();
        ArrayList<P> pairs = merger.createPairs(merger.getClusters().size(), props);
        HashSet<Integer> blacklist = new HashSet<>();
        queue = new FrontHeapQueueMsh(5, blacklist, merger.objectives, props);
        queue.addAll(pairs);

        int i = 0;
        for (P p : queue) {
            assertNotNull(p);
            i++;
        }
        assertEquals(i, queue.size());
    }

}
