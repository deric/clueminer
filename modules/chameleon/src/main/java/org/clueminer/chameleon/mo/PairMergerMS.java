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

import org.clueminer.chameleon.Chameleon;
import org.clueminer.chameleon.GraphCluster;
import org.clueminer.dataset.api.Instance;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Multi-objective merger with dominate-all policy.
 *
 * Uses several optimizations:
 * - items to be added are compared to last front item first
 * - when both objectives are equal to zero, item is not added at all
 *
 * @author deric
 */
@ServiceProvider(service = Merger.class)
public class PairMergerMS<E extends Instance, C extends GraphCluster<E>, P extends MoPair<E, C>> extends PairMergerMOH<E, C, P> implements Merger<E> {

    public static final String NAME = "MOM-DA";

    @Override
    public String getName() {
        return NAME;
    }

    protected void initQueue(Props pref) {
        queue = new FrontHeapQueueDA<>(pref.getInt(Chameleon.NUM_FRONTS, 5), blacklist, objectives, pref);
    }

}
