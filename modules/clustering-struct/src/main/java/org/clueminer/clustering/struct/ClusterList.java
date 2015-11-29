package org.clueminer.clustering.struct;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 * Instead of using this implementation directly it is better to create new
 * clusterings via:  <code>
 *   Clusterings.newList();
 * </code>
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = Clustering.class)
public class ClusterList<E extends Instance, C extends Cluster<E>> implements Clustering<E, C> {

    private static final long serialVersionUID = 5866077228917808995L;
    private C[] data;
    private Props params;
    private final HashMap<String, Integer> name2id;
    private EvaluationTable table;
    /**
     * (n - 1) is index of last inserted item, n itself represents current
     * number of instances in this dataset
     */
    private int n = 0;
    //Lookup
    private final transient InstanceContent instanceContent;
    private final transient AbstractLookup lookup;
    private String name;
    private int id;

    public ClusterList() {
        //some default capacity, to avoid problems with zero array size
        int capacity = 3;
        data = (C[]) new Cluster[capacity];
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        params = new Props();
        name2id = new HashMap<>(capacity);
    }

    public ClusterList(int capacity) {
        if (capacity < 1) {
            //some default capacity, to avoid problems with zero array size
            capacity = 3;
        }
        data = (C[]) new Cluster[capacity];
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        params = new Props();
        name2id = new HashMap<>(capacity);
    }

    public ClusterList(Integer capacity) {
        this(capacity.intValue());
    }

    /**
     * Some identification of clustering doesn't have to be unique, but short
     *
     * @return
     */
    @Override
    public String getName() {
        if (name == null) {
            name = fingerprint();
        }
        return name;
    }

    /**
     * {@inheritDoc }
     *
     * @return
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * {@inheritDoc }
     *
     * @param id
     */
    @Override
    public void setId(int id) {
        this.id = id;
    }

    /**
     * {@inheritDoc }
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    public final void ensureCapacity(int requested) {
        if (requested >= getCapacity()) {
            int capacity = (int) (requested * 1.618); //golden ratio :)
            if (capacity <= size()) {
                capacity = n * 3; // for small numbers due to int rounding we wouldn't increase the size
            }
            if (capacity > data.length) {
                C[] tmp = (C[]) new Cluster[capacity];
                System.arraycopy(data, 0, tmp, 0, data.length);
                data = tmp;
            }
        }
    }

    @Override
    public boolean hasAt(int index) {
        return index >= 0 && index < data.length && data[index] != null;
    }

    @Override
    public boolean add(C e) {
        ensureCapacity(n);
        //cluster numbers start from 0
        e.setClusterId(n);
        ensureName(e);
        name2id.put(e.getName(), n);
        data[n++] = e;
        return true;
    }

    private void ensureName(Cluster<E> e) {
        if (e.getName() == null) {
            //human readable name
            e.setName("cluster " + (e.getClusterId() + 1));
        }
    }

    public int getCapacity() {
        return data.length;
    }

    @Override
    public String getClusterLabel(int i) {
        return get(i).getName();
    }

    /**
     * Numbers of clusters do now always start from 0, this method should return
     * first non-empty cluster
     *
     * @return first non-empty cluster, return null if no such cluster exists
     */
    public Cluster<E> first() {
        if (!isEmpty()) {

            for (Cluster c : data) {
                if (c != null) {
                    return c;
                }
            }
        }
        return null;
    }

    @Override
    public void put(C d) {
        this.add(d);
    }

    @Override
    public void put(int index, C x) {
        ensureCapacity(index);

        if (data[index] == null) {
            n++;
        }
        data[index] = x;
        //cluster numbers start from 1
        x.setClusterId(index);
        ensureName(x);
        name2id.put(x.getName(), index);
    }

    @Override
    public void merge(Cluster... datasets) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        return n;
    }

    /**
     *
     * @return total number of instances
     */
    @Override
    public int instancesCount() {
        int cnt = 0;
        for (Cluster c : data) {
            if (c != null) {
                cnt += c.size();
            }
        }
        return cnt;
    }

    /**
     * Instances are numbered from 0 to {@code instancesCount() - 1}. This
     * method allows access to instances no matter to which cluster are assigned
     *
     * @param i
     * @return i-th instance in the clustering
     */
    @Override
    public E instance(int i) {
        int offset = 0;
        int idx;
        for (Cluster c : data) {
            idx = i - offset;
            if (idx < c.size()) {
                return (E) c.get(idx);
            }
            offset += c.size();
        }

        return null;
    }

    /**
     * Centroid of all clusters is used by some evaluation criterion
     *
     * e.g. {@link org.clueminer.eval.CalinskiHarabasz}
     *
     * @return
     */
    @Override
    public E getCentroid() {
        Cluster<E> first = get(0);
        Instance centroid = first.builder().build(first.attributeCount());

        //TODO: remove this initialization when DoubleVector is fixed
        //initialize with zeros
        for (int i = 0; i < first.attributeCount(); i++) {
            centroid.set(i, 0.0);
        }
        Instance center;
        for (Cluster<E> c : this) {
            center = c.getCentroid();
            for (int i = 0; i < center.size(); i++) {
                //TODO: replace by running average
                centroid.set(i, center.get(i) + centroid.get(i));
            }
        }
        //average of features
        if (this.size() > 1) {
            for (int i = 0; i < first.attributeCount(); i++) {
                centroid.set(i, centroid.value(i) / this.size());
            }
        }
        return (E) centroid;
    }

    @Override
    public Iterator<E> instancesIterator() {
        return new InstancesIterator();
    }

    @Override
    public int[] clusterSizes() {
        int[] clusterSizes = new int[this.size()];
        for (int i = 0; i < this.size(); i++) {
            clusterSizes[i] = get(i).size();
        }
        return clusterSizes;
    }

    /**
     * Returns ID of assigned cluster, if ID not found it could be caused by
     * using methods which didn't supply original mapping (or it's really not
     * present)
     *
     * @param instanceId
     * @return original instance ID
     */
    @Override
    public int assignedCluster(int instanceId) {
        for (Cluster<E> cluster : this) {
            if (cluster.contains(instanceId)) {
                return cluster.getClusterId();
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc }
     *
     * @param inst
     * @return
     */
    @Override
    public C assignedCluster(E inst) {
        for (C cluster : this) {
            if (cluster.contains(inst.getIndex())) {
                return cluster;
            }
        }
        return null;
    }

    @Override
    public C get(int index) {
        return data[index];
    }

    @Override
    public Iterator<C> iterator() {
        return new ClusterIterator();
    }

    @Override
    public boolean isEmpty() {
        return (data == null || size() == 0);
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Cluster) {
            for (Cluster c : this) {
                if (c.equals(o)) {
                    return true;
                }
            }
        } else if (o instanceof Instance) {
            Instance inst;
            for (Iterator<E> iter = this.instancesIterator(); iter.hasNext();) {
                inst = iter.next();
                if (inst.equals(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Object[] toArray() {
        return data;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return (T[]) data;
    }

    public boolean remove(C cluster) {
        //name2id.remove(cluster.getName());
        int idx = cluster.getClusterId();
        if (data[idx] != null && data[idx].equals(cluster)) {
            return remove(idx);
        } else {
            System.out.println("failed to remove " + cluster.getClusterId());
        }
        return false;
    }

    @Override
    public boolean remove(int idx) {
        if (idx > n || idx < 0) {
            return false;
        }
        if (data[idx] != null) {
            if (n == 0) {
                data[idx] = null;
                return true;
            }
            data[idx] = data[n - 1];
            name2id.remove(data[n - 1].getName());
            data[n - 1] = null;
            n--;
            data[idx].setClusterId(idx);
            data[idx].setName("cluster " + (idx + 1));
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        C clust = (C) o;
        return remove(clust);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object elem : c) {
            if (!contains(elem)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean res = true;
        for (Object clust : c) {
            res &= remove(clust);
        }
        return res;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        int capacity = getCapacity();
        data = (C[]) new Cluster[capacity];
        n = 0;
    }

    /**
     * Create new cluster and inserts it at given position
     *
     * @param clusterId
     * @return
     */
    @Override
    public C createCluster(int clusterId) {
        int attrSize = guessAttrCnt();
        C c = (C) new BaseCluster<E>(5, attrSize);
        c.setClusterId(clusterId);
        c.setName("cluster " + (clusterId + 1));
        //some validity measures needs to access attribute properties
        Dataset<E> d = getLookup().lookup(Dataset.class);
        if (d != null) {
            c.setAttributes(d.getAttributes());
        }
        put(clusterId, c);
        return c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C createCluster() {
        int attrSize = guessAttrCnt();
        C c = (C) new BaseCluster<E>(5, attrSize);
        int clusterId = size();
        c.setClusterId(clusterId);
        c.setName("cluster " + (clusterId + 1));
        //some validity measures needs to access attribute properties
        Dataset<E> d = getLookup().lookup(Dataset.class);
        if (d != null) {
            c.setAttributes(d.getAttributes());
        }
        put(clusterId, c);
        return c;
    }

    /**
     * Create new cluster with given ID and capacity
     *
     * @param clusterId
     * @param capacity
     * @return
     */
    @Override
    public C createCluster(int clusterId, int capacity) {
        return createCluster(clusterId, capacity, "cluster " + (clusterId + 1));
    }

    @Override
    public C createCluster(int clusterId, int capacity, String name) {
        int attrSize = guessAttrCnt();
        C c = (C) new BaseCluster<E>(capacity, attrSize);
        c.setClusterId(clusterId);
        c.setName(name);
        put(clusterId, c);
        return c;
    }

    private int guessAttrCnt() {
        int attrCnt = 5; //some default value
        if (data != null && !isEmpty()) {
            if (data[0] != null) {
                attrCnt = data[0].attributeCount();
            }
        }
        return attrCnt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ClusterList(" + size() + ")");
        for (C e : this) {
            sb.append(e.toString());
        }

        return sb.append(getName()).toString();
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void lookupAdd(Object instance) {
        instanceContent.add(instance);
    }

    @Override
    public void lookupRemove(Object instance) {
        instanceContent.remove(instance);
    }

    @Override
    public Props getParams() {
        return params;
    }

    @Override
    public void setParams(Props params) {
        this.params = params;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (Cluster<E> elem : this) {
            hash += elem.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final ClusterList<?, ?> other = (ClusterList<?, ?>) obj;
        if (this.size() != other.size()) {
            return false;
        }
        return Arrays.deepEquals(this.data, other.data);
    }

    /**
     * Will overwrite this with other
     *
     * @param other
     */
    @Override
    public void mergeParams(Props other) {
        params.merge(other);
    }

    @Override
    public C get(String label) {
        if (name2id.containsKey(label)) {
            return get(name2id.get(label));
        }
        return null;
    }

    /**
     * {@inheritDoc }
     *
     * @return
     */
    @Override
    public EvaluationTable getEvaluationTable() {
        return table;
    }

    /**
     * {@inheritDoc }
     *
     * @param table
     */
    @Override
    public void setEvaluationTable(EvaluationTable table) {
        this.table = table;
    }

    /**
     * {@inheritDoc}
     *
     * @return human readable fingerprint
     */
    @Override
    public String fingerprint() {
        int[] sizes = clusterSizes();
        Arrays.sort(sizes);
        return printArray(sizes);
    }

    /**
     * Print element separated by comma, without any space
     *
     * @param a
     * @return
     */
    private String printArray(int[] a) {
        if (a == null) {
            return "null";
        }
        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0;; i++) {
            b.append(a[i]);
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(',');
        }
    }

    @Override
    public void compact() {
        int nonEmpty = 0;
        for (Cluster<E> clust : data) {
            if (clust != null) {
                nonEmpty++;
            }
        }
        C[] newData = (C[]) new Cluster[nonEmpty];
        name2id.clear();
        nonEmpty = 0;
        int idx;
        for (C clust : data) {
            if (clust != null) {
                idx = nonEmpty++;
                newData[idx] = clust;
                clust.setClusterId(idx);
                clust.setName("cluster " + (idx + 1));
                name2id.put(clust.getName(), idx);
            }
        }
        data = newData;
    }

    @Override
    public double getValidation(String metric) {
        return params.getDouble(PropType.VALIDATION, metric, Double.NaN);
    }

    @Override
    public boolean hasValidation(String metric) {
        return params.containsKey(metric);
    }

    @Override
    public void setValidation(String metric, double value) {
        params.put(PropType.VALIDATION, metric, value);
    }

    @Override
    public boolean addAll(Collection<? extends C> c) {
        boolean ret = true;
        for (C member : c) {
            ret &= add(member);
        }
        return ret;
    }

    /**
     * Set name of cluster and update hash table for inverse search
     *
     * @param clusterIndex
     * @param name
     */
    @Override
    public void setClusterName(int clusterIndex, String name) {
        C cluster = data[clusterIndex];
        if (cluster != null) {
            name2id.remove(cluster.getName());
            cluster.setName(name);
            name2id.put(name, clusterIndex);
        }
    }

    class ClusterIterator<X extends Cluster<E>> implements Iterator<X> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public X next() {
            return (X) get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from dataset using the iterator.");

        }
    }

    /**
     * Should iterate over all instances in all clusters
     */
    private class InstancesIterator<E extends Instance> implements Iterator<E> {

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
        public E next() {
            if (j < current.size()) {
                i++;
                return (E) current.instance(j++);
            } else {
                i++;
                j = 0;
                current = get(k++);
                return (E) current.instance(j++);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
