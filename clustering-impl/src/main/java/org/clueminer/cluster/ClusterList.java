package org.clueminer.cluster;

import java.util.ArrayList;
import java.util.Iterator;
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
        return get(i).getName();
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
        for (Cluster c : this) {
            cnt += c.size();
        }
        return cnt;
    }

    @Override
    public E getCentroid() {
        Cluster<E> first = get(0);
        Instance centroid = first.builder().create(first.attributeCount());

        for (Cluster<E> c : this) {
            for (Instance inst : c) {
                //sum all features
                for (int i = 0; i < inst.size(); i++) {
                    centroid.put(i, inst.value(i) + centroid.value(i));
                }
            }
        }

        //average of features
        for (int i = 0; i < first.attributeCount(); i++) {
            centroid.put(i, centroid.value(i) / instancesCount());
        }
        return (E) centroid;
    }

    @Override
    public Iterator<Instance> instancesIterator() {
        return new InstancesIterator();
    }

    /**
     * Should iterate over all instances in all clusters
     */
    private class InstancesIterator implements Iterator<Instance> {

        private int i = 0;
        private int j = 0;
        private int k = 0;
        private Cluster current;

        InstancesIterator() {
            current = get(k++);
        }

        @Override
        public boolean hasNext() {
            return i < instancesCount();
        }

        @Override
        public Instance next() {
            if (j < current.size()) {
                i++;
                return current.instance(j++);
            } else {
                i++;
                j = 0;
                current = get(k++);
                return current.instance(j++);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
