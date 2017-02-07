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
package org.clueminer.export.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author deric
 * @param <E>
 */
public class ClusteringExporterFactory<E extends Instance> extends ServiceFactory<ClusteringExport<E>> {

    private static ClusteringExporterFactory instance;

    public static ClusteringExporterFactory getInstance() {
        if (instance == null) {
            instance = new ClusteringExporterFactory();
        }
        return instance;
    }

    private ClusteringExporterFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends ClusteringExport> list = Lookup.getDefault().lookupAll(ClusteringExport.class);
        for (ClusteringExport c : list) {
            providers.put(c.getName(), c);
        }
    }

    @Override
    public ClusteringExport[] getAllArray() {
        return providers.values().toArray(new ClusteringExport[0]);
    }
}
