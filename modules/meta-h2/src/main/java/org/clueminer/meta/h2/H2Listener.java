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
package org.clueminer.meta.h2;

import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.UpdateFeed;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = UpdateFeed.class)
public class H2Listener implements UpdateFeed {

    private H2Store store;
    public static final String NAME = "H2 store";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int started(Evolution evolution) {
        store = H2Store.getInstance();
        return store.registerRun(evolution);
    }

    @Override
    public void individualCreated(int runId, Individual individual) {
        store.add(runId, individual.getClustering());
    }

}
