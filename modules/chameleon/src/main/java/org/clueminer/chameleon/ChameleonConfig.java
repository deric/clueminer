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
package org.clueminer.chameleon;

import static org.clueminer.chameleon.Chameleon.K;
import static org.clueminer.chameleon.Chameleon.MAX_PARTITION;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 * @param <E>
 */
public class ChameleonConfig<E extends Instance> implements Configurator<E> {

    private static ChameleonConfig instance;

    public static String K_ESTIMATOR = "k-estim";

    private ChameleonConfig() {

    }

    public static ChameleonConfig getInstance() {
        if (instance == null) {
            instance = new ChameleonConfig();
        }
        return instance;
    }

    private void putndef(Props params, String key, Object value) {
        if (!params.containsKey(key)) {
            params.put(key, value);
        }
    }

    @Override
    public void configure(Dataset<E> dataset, Props params) {
        if (!params.containsKey(MAX_PARTITION)) {
            params.putInt(MAX_PARTITION, determineMaxPartitionSize(dataset));
        }
        if (!params.containsKey(K)) {
            params.putInt(K, determineK(dataset, params));
        }
        putndef(params, AlgParams.CUTOFF_SCORE, "SD index");
        putndef(params, AlgParams.CUTOFF_STRATEGY, "FirstJump");
        putndef(params, AlgParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
    }

    /**
     * Estimate parameter k based on logarithm of data size
     *
     * @param dataset
     * @param params
     * @return
     */
    private int determineK(Dataset<E> dataset, Props params) {
        String kEstim = params.get(K_ESTIMATOR, "ln");
        switch (kEstim) {
            case "cln2":
                return (int) Math.ceil(Math.log(dataset.size())) * 2;
            case "cln":
                return (int) (Math.ceil(2 * Math.log(dataset.size())));
            case "fln":
                return (int) (Math.floor(2 * Math.log(dataset.size())));

            case "log10":
                if (dataset.size() < 500) {
                    return (int) (Math.log(dataset.size()) / Math.log(10));
                } else {
                    return (int) (Math.log(dataset.size()) / Math.log(10)) * 2;
                }

            case "ln":

                if (dataset.size() < 500) {
                    return (int) Math.ceil(Math.log(dataset.size()));
                } else {
                    return (int) Math.ceil(Math.log(dataset.size())) * 2;
                }
            case "log2":
                if (dataset.size() < 500) {
                    return (int) (Math.log(dataset.size()) / Math.log(2));
                } else {
                    return (int) (Math.log(dataset.size()) / Math.log(2)) * 2;
                }
            default:
                throw new RuntimeException("unknown method " + kEstim);
        }
    }

    private int determineMaxPartitionSize(Dataset<E> dataset) {
        if (dataset.size() < 500) {
            return 5;
        } else if ((dataset.size() < 2000)) {
            return dataset.size() / 100;
        } else {
            return dataset.size() / 200;
        }
    }

}
