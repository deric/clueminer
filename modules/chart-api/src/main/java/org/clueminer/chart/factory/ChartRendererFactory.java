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
package org.clueminer.chart.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.chart.api.ChartRenderer;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class ChartRendererFactory extends ServiceFactory<ChartRenderer> {

    private static ChartRendererFactory instance;

    public static ChartRendererFactory getInstance() {
        if (instance == null) {
            instance = new ChartRendererFactory();
        }
        return instance;
    }

    private ChartRendererFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends ChartRenderer> list = Lookup.getDefault().lookupAll(ChartRenderer.class);
        for (ChartRenderer c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

    @Override
    public ChartRenderer[] getAllArray() {
        return providers.values().toArray(new ChartRenderer[0]);
    }

}
