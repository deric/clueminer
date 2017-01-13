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
package org.clueminer.graph.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author deric
 */
public class GraphStorageFactory extends ServiceFactory<Graph> {

    private static GraphStorageFactory instance;

    public static GraphStorageFactory getInstance() {
        if (instance == null) {
            instance = new GraphStorageFactory();
        }
        return instance;
    }

    private GraphStorageFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends Graph> list = Lookup.getDefault().lookupAll(Graph.class);
        for (Graph g : list) {
            providers.put(g.getName(), g);
        }
        sort();
    }

    public Graph newInstance(String provider) {
        try {
            Graph c = getProvider(provider);
            return (Graph) c.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
        throw new RuntimeException("failed to instantiate " + provider);
    }

    @Override
    public Graph[] getAllArray() {
        return providers.values().toArray(new Graph[0]);
    }
}
