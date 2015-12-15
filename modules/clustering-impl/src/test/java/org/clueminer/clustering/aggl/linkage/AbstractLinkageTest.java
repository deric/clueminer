package org.clueminer.clustering.aggl.linkage;

import org.clueminer.clustering.aggl.HC;
import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.api.AbstractLinkage;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class AbstractLinkageTest {

    protected AbstractLinkage subject;
    protected static final HC hac = new HC();
    protected static final HCLW haclw = new HCLW();
    protected final double delta = 1e-9;

    protected HierarchicalResult naiveLinkage(Dataset<? extends Instance> dataset) {
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, subject.getName());
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        HierarchicalResult result = hac.hierarchy(dataset, pref);
        return result;
    }

    protected HierarchicalResult lanceWilliamsLinkage(Dataset<? extends Instance> dataset) {
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, subject.getName());
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        HierarchicalResult result = haclw.hierarchy(dataset, pref);
        return result;
    }

}
