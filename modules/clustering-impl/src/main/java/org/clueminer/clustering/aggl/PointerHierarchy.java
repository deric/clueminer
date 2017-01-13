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
package org.clueminer.clustering.aggl;

import java.util.Map;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class PointerHierarchy {

    private final Dataset<? extends Instance> dataset;
    private final Map<Integer, Double> lambda;
    private final int[] pi;

    public PointerHierarchy(Dataset<? extends Instance> dataset, Map<Integer, Double> lambda, int[] pi) {
        this.dataset = dataset;
        this.lambda = lambda;
        this.pi = pi;
    }

    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    public Map<Integer, Double> getLambda() {
        return lambda;
    }

    public int[] getPi() {
        return pi;
    }

}
