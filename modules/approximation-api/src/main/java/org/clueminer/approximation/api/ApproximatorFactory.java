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
package org.clueminer.approximation.api;

import java.util.*;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class ApproximatorFactory extends ServiceFactory<Approximator> {

    private static ApproximatorFactory instance;
    private static List<String> paramNames = null;

    public static ApproximatorFactory getInstance() {
        if (instance == null) {
            instance = new ApproximatorFactory();
        }
        return instance;
    }

    private ApproximatorFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends Approximator> list = Lookup.getDefault().lookupAll(Approximator.class);
        for (Approximator c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

    public List<String> getParamNames() {
        if (paramNames == null) {
            paramNames = new ArrayList<>();
            List<Approximator> appr = getAll();
            for (Approximator a : appr) {
                paramNames.addAll(Arrays.asList(a.getParamNames()));
            }
        }
        return paramNames;
    }

    @Override
    public Approximator[] getAllArray() {
        return providers.values().toArray(new Approximator[0]);
    }
}
