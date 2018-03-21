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
import java.util.LinkedList;
import java.util.List;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.utils.ServiceFactory;

/**
 * Mixes internal and external evaluator
 *
 * @author Tomas Barton
 */
public class EvaluationFactory extends ServiceFactory<ClusterEvaluation> {

    private static EvaluationFactory instance;

    public static EvaluationFactory getInstance() {
        if (instance == null) {
            instance = new EvaluationFactory();
        }
        return instance;
    }

    private EvaluationFactory() {
        providers = new LinkedHashMap<>();
        List<ExternalEvaluator> extern = ExternalEvaluatorFactory.getInstance().getAll();
        List<InternalEvaluator> internal = InternalEvaluatorFactory.getInstance().getAll();

        Collection<ClusterEvaluation> list = new LinkedList<>();
        list.addAll(extern);
        list.addAll(internal);

        for (ClusterEvaluation c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

    @Override
    public ClusterEvaluation[] getAllArray() {
        return providers.values().toArray(new ClusterEvaluation[0]);
    }
}
