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
package org.clueminer.evolution.gui;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class EvolutionExportFactory extends ServiceFactory<EvolutionExport> {

    private static EvolutionExportFactory instance;

    public static EvolutionExportFactory getInstance() {
        if (instance == null) {
            instance = new EvolutionExportFactory();
        }
        return instance;
    }

    private EvolutionExportFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends EvolutionExport> list = Lookup.getDefault().lookupAll(EvolutionExport.class);
        for (EvolutionExport c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

    @Override
    public EvolutionExport[] getAllArray() {
        return providers.values().toArray(new EvolutionExport[0]);
    }
}
