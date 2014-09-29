package org.clueminer.events;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

/**
 * A swing implementation of listeners list does not consider ordering of
 * listeners. This is a proof of concept of "better listeners" mechanism.
 *
 * Currently only simple linear requirements graphs are supported, hopefully
 * this could be extended in the future to handle more complex cases.
 *
 * Note: Requirements graph MUST be DAG!
 *
 * e.g. (a) -> (b) -> (c)
 *
 * @author Tomas Barton
 * @param <T> listener type
 */
public class ListenerList<T> implements Iterable<T> {

    private T[] data;
    private final Map<T, T[]> map;
    private boolean hasConstraints = false;
    private Set<Node<T>> blacklist;
    private int n = -1;

    /**
     * A list of listeners with consistent order
     */
    public ListenerList() {
        map = new LinkedHashMap<>();
    }

    public ListenerList(int capacity) {
        map = new LinkedHashMap<>(capacity);
    }

    public int size() {
        if (n < 0) {
            return map.size();
        }
        return n;
    }

    public final void ensureCapacity(int requested) {
        if ((requested + 1) > getCapacity()) {
            int capacity = (int) (requested * 1.618); //golden ratio :)
            if (capacity <= size()) {
                capacity = size() * 3; // for small numbers due to int rounding we wouldn't increase the size
            }
            if (capacity > data.length) {
                T[] tmp = (T[]) new Object[capacity];
                System.arraycopy(data, 0, tmp, 0, data.length);
                data = tmp;
            }
        }
    }

    public int getCapacity() {
        return data.length;
    }

    /**
     * Adds listener to list
     *
     * @param listner class to be notified
     */
    public void add(T listner) {
        map.put(listner, null);
        clearCache();
    }

    /**
     * Adds listener to list with specified requirements
     *
     * @param listner  class to be notified
     * @param requires which classes should be notified earlier
     */
    public void add(T listner, T[] requires) {
        map.put(listner, requires);
        if (requires != null) {
            hasConstraints = true;
        }
        clearCache();
    }

    /**
     * First argument is always listener, other are instances which needs to be
     * notified before the first one
     *
     * Syntactic sugar interface
     *
     * @param requires (listener, req1, req2, req3, ... )
     */
    public void add(T... requires) {
        if (requires.length < 2) {
            throw new RuntimeException("invalid number of arguments " + requires.length + ". At least 2 arguments are required");
        }
        hasConstraints = true;
        T[] req = (T[]) new Object[requires.length - 1];
        System.arraycopy(requires, 1, req, 0, requires.length - 1);
        map.put(requires[0], req);
        clearCache();
    }

    /**
     * Removes listener from list
     *
     * @param listener
     */
    public void remove(T listener) {
        map.remove(listener);
        clearCache();
    }

    /**
     * Clears created DAG
     */
    private void clearCache() {
        data = null;
    }

    /**
     * Create ordering of listeners which respects constraints
     */
    protected void build() {
        int i = 0;
        n = -1;
        if (!hasConstraints) {
            data = (T[]) new Object[size()];
            //most trivial case (no constraints at all)
            for (T obj : map.keySet()) {
                data[i++] = obj;
            }
        } else {
            Map<T, Node<T>> tmp = buildGraph();
            n = tmp.size();
            //initialize blacklist
            blacklist = new HashSet<>();
            //graph might be bigger than list size
            data = (T[]) new Object[tmp.size()];
            Set<Node<T>> isolated = new HashSet<>();
            //find components with more than one node
            for (Node<T> curr : tmp.values()) {
                //System.out.println("curr = " + curr.toString());
                if (curr.outEdgesCnt() == 0 && curr.inEdgesCnt() == 0) {
                    isolated.add(curr);
                } else {
                    //part of connected component
                    Node<T> root = findComponentRoot(curr);
                    if (!blacklist.contains(root)) {
                        //System.out.println("root = " + root.getValue().toString());
                        writeNode(data, root, i++);
                        i = writeTreeToAnArray(root, data, i);
                    }
                    if (i == blacklist.size()) {
                        //all elements have been written
                        return;
                    }
                }
            }
            //write isolated nodes (in any order)
            for (Node<T> curr : isolated) {
                //System.out.println("isolated: " + curr.toString());
                writeNode(data, curr, i++);
            }
        }
    }

    /**
     * Writes node to result array
     *
     * @param data
     * @param node
     * @param i
     */
    private void writeNode(T[] data, Node<T> node, int i) {
        data[i++] = node.getValue();
        //System.out.println((i - 1) + ": " + node.getValue());
        blacklist.add(node);
    }

    /**
     * Writes root's successors into an array, root itself is not processed
     *
     * @param root
     * @param data
     * @param i
     * @return last written position in the array
     */
    private int writeTreeToAnArray(Node<T> root, T[] data, int i) {
        Iterator<Node<T>> it = root.inIterator();
        Node<T> curr;
        Queue<Node<T>> queue = new ArrayDeque<>();
        //process node's children which are on the same level
        while (it.hasNext()) {
            curr = it.next();
            writeNode(data, curr, i++);
            if (curr.inEdgesCnt() > 0) {
                queue.add(curr);
            }
        }
        // process next level
        if (!queue.isEmpty()) {
            i = writeTreeToAnArray(queue.poll(), data, i);
        }
        return i;
    }

    /**
     * Component is expected to look like a tree (not necessarily binary), root
     * is the node which is only required by other but by itself doesn't have
     * any requirements
     *
     * @param node
     * @return
     */
    private Node<T> findComponentRoot(Node<T> node) {
        //node is the root
        if (node.outEdgesCnt() == 0) {
            return node;
        } else if (node.outEdgesCnt() == 1) {
            return findComponentRoot(node.iterator().next());
        } else {
            throw new RuntimeException("outCnt > 1 is not supported yet");
        }
    }

    /**
     * Create graph of dependencies between listeners
     *
     * @return
     */
    private Map<T, Node<T>> buildGraph() {
        Map<T, Node<T>> tmp = new LinkedHashMap<>();
        for (Entry<T, T[]> entry : map.entrySet()) {
            //no constraints specified
            if (entry.getValue() == null) {
                tmp.put(entry.getKey(), new Node<>(entry.getKey()));
            } else {
                Node<T> curr;
                T key = entry.getKey();
                if (tmp.containsKey(key)) {
                    curr = tmp.get(key);
                } else {
                    curr = new Node<>(key);
                    tmp.put(key, curr);
                }
                T[] requires = entry.getValue();
                Node<T> dep;
                for (T req : requires) {
                    if (tmp.containsKey(req)) {
                        dep = tmp.get(req);
                    } else {
                        dep = new Node<>(req);
                        tmp.put(req, dep);
                    }
                    curr.addOutEdge(dep);
                }
            }
        }
        return tmp;
    }

    protected T get(int index) {
        if (data == null) {
            build();
        }
        return data[index];
    }

    public T[] getListeners() {
        if (data == null) {
            build();
        }
        return data;
    }

    public <T> T[] toArray(T[] a) {
        getListeners(); //make sure graph is built
        if (a.length < size()) // Make a new array of a's runtime type, but my contents:
        {
            return (T[]) Arrays.copyOf(data, size(), a.getClass());
        }
        System.arraycopy(data, 0, a, 0, size());
        if (a.length > size()) {
            a[size()] = null;
        }
        return a;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Iterator<T> iterator() {
        if (data == null) {
            build();
        }
        return new ListenerIterator();
    }

    class ListenerIterator implements Iterator<T> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public T next() {
            return get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from dataset using the iterator.");

        }
    }

}
