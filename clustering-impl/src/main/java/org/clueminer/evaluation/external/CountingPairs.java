package org.clueminer.evaluation.external;

import com.google.common.base.Supplier;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.util.Map;
import java.util.TreeMap;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class CountingPairs {

    /**
     * Should count number of item with same assignment to <Cluster A, Class X>
     * Instances must have included information about class assignment. This
     * table is sometimes called contingency table
     *
     * Classes are in rows, Clusters are in columns
     *
     * @param clustering
     * @return table with counts of items for each pair cluster, class
     */
    public static Table<String, String, Integer> contingencyTable(Clustering<Cluster> clustering) {
        // a lookup table for storing correctly / incorrectly classified items
        Table<String, String, Integer> table = Tables.newCustomTable(
                Maps.<String, Map<String, Integer>>newHashMap(),
                new Supplier<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> get() {
                return Maps.newHashMap();
            }
        });

        //Cluster current;
        Instance inst;
        String cluster, label;
        int cnt;
        for (Cluster<Instance> current : clustering) {
            for (int i = 0; i < current.size(); i++) {
                inst = current.instance(i);
                cluster = current.getName();
                label = inst.classValue().toString();

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
     * Guesses which cluster corresponds to which class (this could be done by
     * set intersect, but it would be very expensive)
     *
     * @TODO there's set intersection in Guava:
     * http://docs.guava-libraries.googlecode.com/git-history/v14.0/javadoc/com/google/common/collect/Sets.html#intersection%28java.util.Set,%20java.util.Set%29
     *
     * @param table
     * @return
     */
    public static BiMap<String, String> findMatching(Table<String, String, Integer> table) {
        BiMap<String, String> matching = HashBiMap.create(table.size());

        //sort clusters by number of diverse classes inside, clusters containing 
        //only one class will have priority
        TreeMap<String, Integer> sortedClusters = new ValueComparableMap<String, Integer>(Ordering.natural());

        for (String rowKey : table.rowKeySet()) {
            sortedClusters.put(rowKey, table.row(rowKey).size());
        }
        //for each real class we have to find best match
        for (String cluster : sortedClusters.keySet()) {
            Map<String, Integer> assign = table.row(cluster);
            int max = 0, value;
            String maxKey = null;
            for (String key : assign.keySet()) {
                value = assign.get(key);
                //if one class would have same number of assignments to two 
                //clusters, it's hard to decide which is which                
                if (value >= max && !matching.containsKey(key)) {
                    max = value;
                    maxKey = key;
                }
            }
            //one class could be assigned just to one cluster - hard membership
            //it's not guaranteed that we'll find matching class for each cluster
            if (maxKey != null && !matching.containsKey(maxKey)) {
                matching.put(maxKey, cluster);
            }
        }
        return matching;
    }

    /**
     *  - TP (true positive) - as the number of points that are present in the same cluster in both C1 and C2.
     *  - FP (false positive) - as the number of points that are present in the same cluster in C1 but not in C2.
     *  - FN (false negative) - as the number of points that are present in the same cluster in C2 but not in C1.
     *  - TN (true negative) - as the number of points that are in different clusters in both C1 and C2.
     * @param table
     * @param matching
     * @param clusterName
     * @return table containing positive/negative assignments (usually used in
     * supervised learning)
     */
    public static Map<String, Integer> countAssignments(Table<String, String, Integer> table, String realClass, String clusterName) {
        int tp, fp = 0, fn = 0, tn = 0;
        int value;
        //inverse map allows searching by value
        //String realClass = matching.inverse().get(clusterName);
        
        //true positive
        tp = table.get(clusterName, realClass);

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
}
