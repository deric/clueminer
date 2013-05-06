package org.clueminer.cluster;

import java.util.ArrayList;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class ClusterList<E extends Instance> extends ArrayList<Cluster<E>> implements Clustering<Cluster<E>> {

    private static final long serialVersionUID = 5866077228917808994L;

    public ClusterList(int capacity) {
        super(capacity);
    }

    @Override
    public boolean hasAt(int index) {
        if (index > (this.size() - 1)) {
            return false;
        }
        return true;
    }

    @Override
    public String getClusterLabel(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void put(Cluster d) {
        this.add(d);
    }

    @Override
    public void put(int index, Cluster d) {
        this.add(index, d);
    }

    @Override
    public void merge(Cluster... datasets) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * 
     * @return total number of instances
     */
    @Override
    public int instancesCount() {
        int cnt = 0;
        for(Cluster c : this){
            cnt += c.size();
        }
        return cnt;
    }
}
