package org.clueminer.clustering.api;

import org.clueminer.distance.api.DistanceMeasure;

/**
 * The method to use when comparing the similarity of two clusters. See <a
 * href="http://home.dei.polimi.it/matteucc/Clustering/tutorial_html/hierarchical.html">
 * here </a> for an example of how the different linkages operate.
 *
 * @author Tomas Barton
 */
public abstract class AbstractLinkage implements ClusterLinkage {

    private static final long serialVersionUID = -4024535548255187696L;
    protected DistanceMeasure distanceMeasure;
    
    public AbstractLinkage(DistanceMeasure dm){
        this.distanceMeasure = dm;
    }

    public DistanceMeasure getDistanceMeasure() {
        return distanceMeasure;
    }

    @Override
    public void setDistanceMeasure(DistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
    }
}
