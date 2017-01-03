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
package org.clueminer.eval.utils;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.InvalidClustering;
import org.clueminer.clustering.api.ScoreException;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 * Count common assignments between two clusterings.
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class CountingPairs<E extends Instance, C extends Cluster<E>> {

    private static final String UNKNOWN_LABEL = "unknown";
    private static CountingPairs instance;

    private CountingPairs() {

    }

    public static CountingPairs getInstance() {
        if (instance == null) {
            instance = new CountingPairs();
        }
        return instance;
    }

    public static Table<String, String, Integer> newTable() {
        return Tables.newCustomTable(
                Maps.<String, Map<String, Integer>>newHashMap(),
                new Supplier<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> get() {
                return Maps.newHashMap();
            }
        });
    }

    /**
     * Should count number of item with same assignment to <Cluster A, Class X>
     * Instances must have included information about class assignment. This
     * table is sometimes called contingency table
     *
     * Classes are in columns, Clusters are in rows
     *
     * @param clustering
     * @return table with counts of items for each pair cluster, class
     */
    public static Table<String, String, Integer> contingencyTable(Clustering<? extends Instance, ? extends Cluster> clustering) {
        // tp lookup table for storing correctly / incorrectly classified items
        Table<String, String, Integer> table = newTable();

        //Cluster current;
        Instance inst;
        String cluster, label;
        int cnt;
        for (Cluster current : clustering) {
            for (int i = 0; i < current.size(); i++) {
                inst = current.instance(i);
                cluster = current.getName();
                Object klass = inst.classValue();
                if (klass != null) {
                    label = klass.toString();
                } else {
                    label = UNKNOWN_LABEL;
                }

                if (table.contains(cluster, label)) {
                    cnt = table.get(cluster, label);
                } else {
                    cnt = 0;
                }

                cnt++;
                table.put(cluster, label, cnt);
            }
        }
        return table;
    }

    /**
     * Find number of common instances in each cluster of A and B
     *
     * @param c1 clustering A
     * @param c2 clustering B - reference cluster (considered as "correct")
     * @return
     */
    public static Table<String, String, Integer> contingencyTable(
            Clustering<? extends Instance, ? extends Cluster> c1, Clustering<? extends Instance, ? extends Cluster> c2) {
        // tp lookup table for storing same / differently classified items
        Table<String, String, Integer> table = newTable();

        //Cluster current;
        String label1, label2;
        int cnt;
        for (Cluster<? extends Instance> a : c1) {
            for (Cluster<? extends Instance> b : c2) {

                label1 = a.getName();
                label2 = b.getName();

                if (!table.contains(label1, label2)) {
                    Set<? extends Instance> tp = Sets.intersection(a, b);
                    cnt = tp.size();
                    if (cnt > 0) {
                        table.put(label1, label2, cnt);
                    }
                }
            }
        }
        return table;
    }

    /**
     * Guesses which cluster corresponds to which class
     *
     * @param table contingency table
     * @return
     */
    public static Matching findMatching(Table<String, String, Integer> table) {
        //     class, cluster
        Matching matching = new Matching();

        //sort clusters by number of diverse classes inside, clusters containing
        //only one class will have priority
        TreeMap<String, Integer> sortedClusters = new ValueComparableMap<>(Ordering.natural());

        for (String rowKey : table.rowKeySet()) {
            sortedClusters.put(rowKey, table.row(rowKey).size());
        }
        LinkedList<String> notAssigned = new LinkedList<>();
        //for each real class we have to find best match
        for (String cluster : sortedClusters.keySet()) {
            Map<String, Integer> assign = table.row(cluster);
            int max = 0, value;
            String maxKey = null;
            for (String klass : assign.keySet()) {
                value = assign.get(klass);
                //if one class would have same number of assignments to two
                //clusters, it's hard to decide which is which
                if (value >= max && !matching.containsKey(klass)) {
                    max = value;
                    maxKey = klass;
                }
            }
            //one class could be assigned just to one cluster - hard membership
            //it's not guaranteed that we'll find matching class for each cluster
            if (maxKey == null) {
                notAssigned.push(cluster);
            } else if (!matching.containsKey(maxKey)) {
                matching.put(maxKey, cluster);
            } else {
                throw new RuntimeException("this should not happen");
            }
        }
        //some cluster hasn't been assigned to tp class
        // matching.size() < sortedClusters.size()
        if (notAssigned.size() > 0) {
            for (String cluster : notAssigned) {
                for (String klass : table.columnKeySet()) {
                    if (!matching.containsKey(klass)) {
                        matching.put(klass, cluster);
                        break;
                    }
                }
            }
        }

        //number of matching classes is lower than actual
        if (matching.size() < table.columnKeySet().size()) {
            //check if all classes has been assigned to tp cluster
            for (String klass : table.columnKeySet()) {
                if (!matching.containsKey(klass)) {
                    int max = 0, value;
                    String maxKey = null;
                    for (String cluster : sortedClusters.keySet()) {
                        Map<String, Integer> assign = table.row(cluster);
                        if (assign.containsKey(klass)) {
                            value = assign.get(klass);
                            if (value > max) {
                                max = value;
                                maxKey = cluster;
                            }
                        }
                    }
                    if (maxKey != null) {
                        matching.put(klass, maxKey);
                    } else {
                        System.out.println("failed to assign class to any cluster: " + klass);
                    }

                    //break;
                }
            }
        }

        return matching;
    }

    /**
     * - TP (true positive) - as the number of points that are present in the
     * same cluster in both C1 and C2. - FP (false positive) - as the number of
     * points that are present in the same cluster in C1 but not in C2. - FN
     * (false negative) - as the number of points that are present in the same
     * cluster in C2 but not in C1. - TN (true negative) - as the number of
     * points that are in different clusters in both C1 and C2.
     *
     * @param table
     * @param realClass
     * @param clusterName
     * @return table containing positive/negative assignments (usually used in
     *         supervised learning)
     */
    public static Map<String, Integer> countAssignments(Table<String, String, Integer> table, String realClass, String clusterName) {
        int tp, fp = 0, fn = 0, tn = 0;
        int value;
        //inverse map allows searching by value
        //String realClass = matching.inverse().get(clusterName);

        //true positive
        if (table.contains(clusterName, realClass)) {
            tp = table.get(clusterName, realClass);
        } else {
            tp = 0;
        }

        //interate over clusters
        for (String clust : table.rowKeySet()) {
            Map<String, Integer> row = table.row(clust);

            if (clust.equals(clusterName)) {
                for (String klass : row.keySet()) {
                    if (!klass.equals(realClass)) {
                        fn += table.get(clusterName, klass);
                    }
                    //else true positive (already counted)
                }
            } else {
                for (String klass : row.keySet()) {
                    value = row.get(klass);
                    if (klass.equals(realClass) || clust.equals(clusterName)) {
                        fp += value;
                    } else {
                        tn += value;
                    }
                }
            }
        }

        //an immutable version of map
        ImmutableMap<String, Integer> res = ImmutableMap.<String, Integer>builder()
                .put("tp", tp)
                .put("fp", fp)
                .put("fn", fn)
                .put("tn", tn)
                .build();

        return res;
    }

    public static Clustering<? extends Instance, ? extends Cluster> clusteringFromClasses(Clustering clust) {
        Clustering<? extends Instance, ? extends Cluster> golden = null;

        Dataset<? extends Instance> dataset = clust.getLookup().lookup(Dataset.class);
        if (dataset != null) {
            SortedSet set = dataset.getClasses();
            golden = Clusterings.newList();
            //golden.lookupAdd(dataset);
            EvaluationTable evalTable = new HashEvaluationTable(golden, dataset);
            golden.lookupAdd(evalTable);
            HashMap<Object, Integer> map = new HashMap<>(set.size());
            Object obj;
            Iterator it = set.iterator();
            int i = 0;
            Cluster c;
            while (it.hasNext()) {
                obj = it.next();
                c = golden.createCluster(i);
                c.setAttributes(dataset.getAttributes());
                map.put(obj, i++);
            }
            int assign;

            for (Instance inst : dataset) {
                if (inst.classValue() == null) {
                    throw new RuntimeException("missing class value");
                } else {
                    if (map.containsKey(inst.classValue())) {
                        assign = map.get(inst.classValue());
                        c = golden.get(assign);
                    } else {
                        c = golden.createCluster(i);
                        c.setAttributes(dataset.getAttributes());
                        map.put(inst.classValue(), i++);
                    }
                    c.add(inst);
                }
            }
        }
        return golden;
    }

    public static void dumpTable(Table<String, String, Integer> table) {
        StringBuilder sb = new StringBuilder();
        Set<String> rows = table.columnKeySet();
        Set<String> cols = table.rowKeySet();
        String separator = "   ";
        //print header
        sb.append(separator);
        for (String col : cols) {
            sb.append(col);
            sb.append(separator);
        }
        sb.append("\n");
        for (String row : rows) {
            sb.append(row);
            sb.append(separator);
            for (String col : cols) {
                sb.append(table.get(col, row));
                sb.append(separator);
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    /**
     * Match instances in two clusterings of the same dataset. From resulting
     * table we can tell how close the {@code curr} clustering is to the
     * reference ({@code  ref) one.
     *
     * @param ref  reference clustering,
     * @param curr second clustering
     *
     * @return
     */
    public PairMatch matchPairs(Clustering<E, C> ref, Clustering<E, C> curr) {
        PairMatch pm = new PairMatch();

        E x, y;
        Cluster<E> cx1, cx2, cy1, cy2;
        for (int i = 0; i < ref.instancesCount(); i++) {
            x = ref.instance(i);
            cx1 = ref.assignedCluster(x);
            cx2 = curr.assignedCluster(x);
            for (int j = 0; j < i; j++) {
                y = ref.instance(j);
                cy1 = ref.assignedCluster(y);
                cy2 = curr.assignedCluster(y);
                //in C1 both are in the same cluster
                if (cx1.getClusterId() == cy1.getClusterId()) {
                    if (cx2.getClusterId() == cy2.getClusterId()) {
                        pm.tp++;
                    } else {
                        pm.fn++;
                    }
                } else if (cx2.getClusterId() == cy2.getClusterId()) {
                    pm.fp++;
                } else {
                    pm.tn++;
                }
            }
        }
        return pm;
    }

    /**
     * Match clustering against class labels
     *
     * @param clust
     * @return
     * @throws org.clueminer.clustering.api.ScoreException
     */
    public PairMatch matchPairs(Clustering<E, C> clust) throws ScoreException {
        PairMatch pm = new PairMatch();

        Dataset<E> dataset = clust.getLookup().lookup(Dataset.class);
        if (dataset == null) {
            throw new ScoreException("missing reference dataset");
        }
        if (dataset.getClasses().isEmpty()) {
            //no labels provided in the dataset
            throw new ScoreException("missing labels for dataset " + dataset.getName());
        }

        E x, y;
        Cluster<E> cx2, cy2;
        //class labels
        Object cx1, cy1;
        for (int i = 0; i < dataset.size() - 1; i++) {
            x = dataset.get(i);
            cx2 = clust.assignedCluster(x);
            if (cx2 == null) {
                throw new InvalidClustering("instance " + x.getIndex()
                        + " from dataset " + dataset.getName() + " is not assigned to any cluster: " + clust.getParams().toString());
            }
            cx1 = x.classValue();
            for (int j = i + 1; j < dataset.size(); j++) {
                y = dataset.get(j);
                cy1 = y.classValue();
                cy2 = clust.assignedCluster(y);
                if (cy2 == null) {
                    throw new InvalidClustering("instance " + y.getIndex()
                            + " from dataset " + dataset.getName() + " is not assigned to any cluster, " + clust.getParams().toString());
                }
                //in both instances have the same label
                if (cx1.equals(cy1)) {
                    //both instances are assigned to the same cluster
                    if (cx2.getClusterId() == cy2.getClusterId()) {
                        pm.tp++;
                    } else {
                        pm.fp++;
                    }
                } else if (cx2.getClusterId() == cy2.getClusterId()) {
                    pm.fn++;
                } else {
                    pm.tn++;
                }
            }
        }
        return pm;
    }
}
