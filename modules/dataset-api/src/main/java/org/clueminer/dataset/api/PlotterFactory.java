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
package org.clueminer.dataset.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author deric
 */
public class PlotterFactory extends ServiceFactory<Plotter> {

    private static PlotterFactory instance;

    public static PlotterFactory getInstance() {
        if (instance == null) {
            instance = new PlotterFactory();
        }
        return instance;
    }

    private PlotterFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends Plotter> list = Lookup.getDefault().lookupAll(Plotter.class);
        for (Plotter c : list) {
            providers.put(c.getClass().getName(), c);
        }
        sort();
    }

    public Collection<Plotter> filter(DataType type) {
        Collection<Plotter> res = new LinkedList<>();
        for (Plotter p : getAll()) {
            if (p.isSupported(type)) {
                res.add(p);
            }
        }
        return res;
    }

    @Override
    public Plotter[] getAllArray() {
        return providers.values().toArray(new Plotter[0]);
    }
}
