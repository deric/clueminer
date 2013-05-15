package org.clueminer.evaluation.external;

import com.google.common.base.Supplier;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class CountingPairs {

    /**
     * number of pairs of points with the same label in C and assigned to the
     * same cluster in K
     */
    protected int a = 0;
    /**
     * the number of pairs with the same label, but in different clusters
     */
    protected int b = 0;
    /**
     * number of pairs in the same cluster, but with different class labels
     */
    protected int c = 0;
    /**
     * number of pairs with different label and different cluster
     */
    protected int d = 0;

    /**
     * Should count number of item with same assignment to <Cluster A, Class X>
     * Instances must have included information about class assignment
     *
     * Classes are in rows, Clusters are in columns
     *
     * @param clustering
     * @return table with counts of items for each pair cluster, class
     */
    public Table<String, String, Integer> countPairs(Clustering<Cluster> clustering) {
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

                if (table.contains(label, cluster)) {
                    cnt = table.get(label, cluster);
                } else {
                    cnt = 0;
                }

                cnt++;
                table.put(label, cluster, cnt);
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
    public BiMap<String, String> findMatching(Table<String, String, Integer> table) {
        BiMap<String, String> matching = HashBiMap.create(table.size()); //new HashBiMap<String, String>(table.size());

        //for each real class we have to find best match
        for (String r : table.rowKeySet()) {
            Map<String, Integer> assign = table.row(r);
            int max = 0, value;
            String maxKey = null;
            for (String key : assign.keySet()) {
                value = assign.get(key);
                //if one class would have same number of assignments to two 
                //clusters, it's hard to decide which is which
                if (value > max && !matching.containsKey(key)) {
                    max = value;
                    maxKey = key;
                }
            }
            matching.put(r, maxKey);
        }
        return matching;
    }

    public Map<String, Integer> countAssignments(Table<String, String, Integer> table, BiMap<String, String> matching, String clusterName) {
        int tp, fp = 0, fn = 0, tn = 0;
        int value;
        //inverse map allows searching by value
        String realClass = matching.inverse().get(clusterName);
        //true positive
        tp = table.get(realClass, clusterName);

        //interate over clusters
        for (String clust : table.columnKeySet()) {
            Map<String, Integer> column = table.column(clust);
            
            if (clust.equals(clusterName)) {
                for (String klass : column.keySet()) {
                    if (!klass.equals(realClass)) {
                        fn += table.get(klass, clusterName);
                    }
                    //else true positive (already counted)
                }
            } else {
                for (String klass : column.keySet()) {
                    value = column.get(klass);
                    if (klass.equals(realClass) || clust.equals(clusterName)) {
                        fp += value;                                            
                    } else {
                        tn += value;
                    }                    
                }
            }
        }

        Map<String, Integer> res = new HashMap<String, Integer>(4);
        res.put("tp", tp);
        res.put("fp", fp);
        res.put("fn", fn);
        res.put("tn", tn);

        return res;
    }

    protected void countPairs2(Clustering<Cluster> clustering) {
        /*     int datasetSize = clustering.instancesCount();
         Instance point1;
         Instance point2;
         for (int i = 0; i < datasetSize - 1; i++) {
         for (int j = i + 1; j < datasetSize; j++) {
         point1 = clustering.get(i);
         point2 = clustering.get(j);

         if (point1.getLabel() == point2.getLabel()) {
         //points have same label 
         if (point1.getCalculatedClusternumber() == point2.getCalculatedClusternumber()) {
         //points assigned to same cluster
         a++;
         } else {
         b++;
         }
         } else {
         //different label
         if (point1.getCalculatedClusternumber() == point2.getCalculatedClusternumber()) {
         //same calculated cluster
         c++;
         } else {
         d++; //not really used to calculate index. just a control variable

         }
         }
         }
         }
         assert ((a + b + c + d) == datasetSize);*/
    }
}
