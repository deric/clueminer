package org.clueminer.explorer;

import java.util.ArrayList;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringChildren extends Children.Keys {

    private Clustering<Cluster> clustering;
    private ArrayList list = new ArrayList();

    public ClusteringChildren() {
    }

    public ClusteringChildren(Clustering<Cluster> clusters) {
        this.clustering = clusters;

        setKeys(clusters);
    }

    @Override
    protected Node[] createNodes(Object key) {
        Clustering obj = (Clustering) key;
        list.add(key);
        return new Node[]{new ClusteringNode(obj)};
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        /*  ClusteringResult[] objs = new ClusteringResult[clustering.length];
         for (int i = 0; i < objs.length; i++) {
         ClusteringResult cat = new ClusteringResult();
         cat.setName(Categories[i]);
         objs[i] = cat;
         }
         setKeys(objs);*/
        setKeys(list);
    }
}
