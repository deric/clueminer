package org.clueminer.explorer;

import java.util.Collection;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.evolution.Evolution;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringChildren extends Children.Keys<Clustering> {

    private Lookup.Result<Clustering> result;

    public ClusteringChildren() {

    }

    public ClusteringChildren(Evolution alg) {
        result = alg.getLookup().lookupResult(Clustering.class);
        result.addLookupListener(new LookupListener() {
            @Override
            public void resultChanged(LookupEvent evt) {
                System.out.println("clust child lookup event! " + evt);
                addNotify();
            }
        });

    }

    @Override
    protected Node[] createNodes(Clustering key) {
        return new Node[]{new ClusteringNode(key)};
    }

    @Override
    protected void addNotify() {
        if (result != null) {
            Collection<? extends Clustering> coll = result.allInstances();
            if (coll != null && coll.size() > 0) {
                setKeys(coll);
            }
        } else {
            System.out.println("coll result is null!");
        }
    }

}
