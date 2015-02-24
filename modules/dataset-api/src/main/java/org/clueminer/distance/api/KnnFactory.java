/*
 * Copyright (C) 2015 clueminer.org
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
package org.clueminer.distance.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class KnnFactory extends ServiceFactory<KNN> {

    private static KnnFactory instance;

    public static KnnFactory getInstance() {
        if (instance == null) {
            instance = new KnnFactory();
        }
        return instance;
    }

    private KnnFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends KNN> list = Lookup.getDefault().lookupAll(KNN.class);
        for (KNN c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
