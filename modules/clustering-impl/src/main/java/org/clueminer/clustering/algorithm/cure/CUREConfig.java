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
package org.clueminer.clustering.algorithm.cure;

import org.clueminer.clustering.api.Configurator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 * @param <E>
 */
public class CUREConfig<E extends Instance> implements Configurator<E> {

    private static CUREConfig instance;

    private CUREConfig() {

    }

    public static CUREConfig getInstance() {
        if (instance == null) {
            instance = new CUREConfig();
        }
        return instance;
    }

    @Override
    public void configure(Dataset<E> dataset, Props params) {
        params.putInt(CURE.K, (int) Math.sqrt(dataset.size()));
        if (!params.containsKey(CURE.SHRINK_FACTOR)) {
            params.putDouble(CURE.SHRINK_FACTOR, 0.5);
        }
    }

}
