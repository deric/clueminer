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
package org.clueminer.clustering.api.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public class LinkageFactory<E extends Instance> extends ServiceFactory<ClusterLinkage<E>> {

    private static LinkageFactory instance;

    public static LinkageFactory getInstance() {
        if (instance == null) {
            instance = new LinkageFactory();
        }
        return instance;
    }

    private LinkageFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends ClusterLinkage> list = Lookup.getDefault().lookupAll(ClusterLinkage.class);
        for (ClusterLinkage c : list) {
            providers.put(c.getName(), c);
        }
    }

    @Override
    public ClusterLinkage<E>[] getAllArray() {
        return providers.values().toArray(new ClusterLinkage[0]);
    }
}
