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
package org.clueminer.clustering.api;

import org.clueminer.clustering.api.factory.LinkageFactory;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.utils.Props;

/**
 * Collection of string constants for common algorithm parameters naming.
 * Constants are used as keys in key-value parameter's storage {@link Props}
 *
 * @author Tomas Barton
 * @param <E>
 */
public class AlgParams<E extends Instance> {

    /**
     * Linkage method, see classes implementing
     * {@link org.clueminer.clustering.api.ClusterLinkage} for available values
     */
    public static final String LINKAGE = "hac-linkage";

    public static final String DEFAULT_DISTANCE_FUNCTION = "Euclidean";

    public static final String DEFAULT_LINKAGE = "Complete";

    /**
     * cluster rows (default)
     */
    public static final String CLUSTERING_TYPE = "clustering_type";

    /**
     * Input matrix standardization method {@link org.clueminer.dataset.api.DataStawndardization}
     */
    public static final String STD = "std";

    /**
     * Whether to use logarithmic scaling - boolean parameter
     */
    public static final String LOG = "log-scale";

    /**
     * Algorithm name
     */
    public static final String ALG = "algorithm";

    public static final String DIST = "distance";

    /**
     * Cutoff value
     */
    public static final String CUTOFF = "cutoff";
    /**
     * Strategy for selecting cutoff, typically dependent on cutoff-score
     */
    public static final String CUTOFF_STRATEGY = "cutoff-strategy";
    /**
     * Evaluation function which could be used for determining quality of cutoff
     */
    public static final String CUTOFF_SCORE = "cutoff-score";
    /**
     * Boolean - whether to keep precomputed proximity matrix for further
     * computations
     */
    public static final String KEEP_PROXIMITY = "keep-proximity-matrix";

    /**
     * First merge items with lowest similarity value
     */
    public static final String SMALLEST_FIRST = "smallest-first";

    private Props pref;

    private Distance distance;

    public AlgParams(Props props) {
        this.pref = props;
        init();
    }

    private void init() {
        distance = getDistanceMeasure();
    }

    public Props getPref() {
        return pref;
    }

    public void setPref(Props pref) {
        this.pref = pref;
    }

    public Distance getDistanceMeasure() {
        String simFuncProp = pref.get(DIST, DEFAULT_DISTANCE_FUNCTION);
        return DistanceFactory.getInstance().getProvider(simFuncProp);
    }

    public ClusterLinkage getLinkage() {
        String linkageProp = pref.get(LINKAGE, DEFAULT_LINKAGE);
        ClusterLinkage<E> linkage = (ClusterLinkage<E>) LinkageFactory.getInstance().getProvider(linkageProp);
        linkage.setDistanceMeasure(distance);
        return linkage;
    }

    public boolean clusterRows() {
        if (pref.containsKey(CLUSTERING_TYPE)) {
            return ClusteringType.parse(pref.getObject(CLUSTERING_TYPE)) != ClusteringType.COLUMNS_CLUSTERING;
        }
        return true; //by default cluster rows
    }

    public boolean clusterColumns() {
        if (pref.containsKey(CLUSTERING_TYPE)) {
            return ClusteringType.parse(pref.getObject(CLUSTERING_TYPE)) == ClusteringType.COLUMNS_CLUSTERING;
        }
        return false; //by default cluster rows
    }

}
