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
package org.clueminer.evolution.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class UpdateFeedFactory extends ServiceFactory<UpdateFeed> {

    private static UpdateFeedFactory instance;

    public static UpdateFeedFactory getInstance() {
        if (instance == null) {
            instance = new UpdateFeedFactory();
        }
        return instance;
    }

    private UpdateFeedFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends UpdateFeed> list = Lookup.getDefault().lookupAll(UpdateFeed.class);
        for (UpdateFeed mf : list) {
            providers.put(mf.getName(), mf);
        }
        sort();
    }

    @Override
    public UpdateFeed[] getAllArray() {
        return providers.values().toArray(new UpdateFeed[0]);
    }
}
