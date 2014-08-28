package org.clueminer.clustering.api;

import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class AgglParams {
    public static final String LINKAGE = "hac-linkage";

    private static final String DEFAULT_DISTANCE_FUNCTION = "Euclidean";

    public static final String DEFAULT_LINKAGE = "Complete Linkage";

    /**
     * either we are clustering rows or columns
     */
    public static final String CLUSTER_ROWS = "cluster_rows";

    /**
     * Input matrix standardization method
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
    public static final String CUTOFF_STRATEGY = "cutoff-strategy";
    public static final String CUTOFF_SCORE = "cutoff-score";

    private Props pref;

    private DistanceMeasure distance;

    public AgglParams(Props props) {
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

    public DistanceMeasure getDistanceMeasure() {
        String simFuncProp = pref.get(DIST, DEFAULT_DISTANCE_FUNCTION);
        return DistanceFactory.getInstance().getProvider(simFuncProp);
    }

    public ClusterLinkage getLinkage() {
        String linkageProp = pref.get(LINKAGE, DEFAULT_LINKAGE);
        ClusterLinkage linkage = LinkageFactory.getInstance().getProvider(linkageProp);
        linkage.setDistanceMeasure(distance);
        return linkage;
    }

    public boolean clusterRows() {
        return pref.getBoolean(CLUSTER_ROWS, true);
    }

    public boolean clusterColumns() {
        return !clusterRows();
    }

}
