package org.clueminer.explorer;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static final Logger logger = Logger.getLogger(ClusteringChildren.class.getName());

    public ClusteringChildren() {

    }

    public ClusteringChildren(Evolution alg) {
        result = alg.getLookup().lookupResult(Clustering.class);
        result.addLookupListener(new LookupListener() {
            @Override
            public void resultChanged(LookupEvent evt) {
                logger.log(Level.INFO, "clust child lookup event! {0}", evt);
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
            logger.log(Level.SEVERE, "clustering result is null!");
        }
    }

}
