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
package org.clueminer.clustering.api.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 * Instantiates unsupervised clustering evaluators
 *
 * @see InternalEvaluator.
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class InternalEvaluatorFactory<E extends Instance, C extends Cluster<E>> extends ServiceFactory<InternalEvaluator<E, C>> {

    private static InternalEvaluatorFactory instance;

    public static InternalEvaluatorFactory getInstance() {
        if (instance == null) {
            instance = new InternalEvaluatorFactory();
        }
        return instance;
    }

    private InternalEvaluatorFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends InternalEvaluator<E, C>> list = (Collection<? extends InternalEvaluator<E, C>>) Lookup.getDefault().lookupAll(InternalEvaluator.class);
        for (InternalEvaluator<E, C> c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

    @Override
    public InternalEvaluator<E, C>[] getAllArray() {
        return providers.values().toArray(new InternalEvaluator[0]);
    }
}
