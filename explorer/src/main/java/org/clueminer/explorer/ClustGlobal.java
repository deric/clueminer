package org.clueminer.explorer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Clustering;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Tomas Barton
 */
public class ClustGlobal extends Children.Keys<Clustering> {

    private Lookup.Result<Clustering> result;
    private static final Logger logger = Logger.getLogger(ClustGlobal.class.getName());
    private Set<Clustering> all = new HashSet<Clustering>(5);

    public ClustGlobal() {

    }

    public ClustGlobal(Lookup.Result<Clustering> result) {
        this.result = result;
        this.result.addLookupListener(new LookupListener() {
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
                all.addAll(coll);
                setKeys(all);
            }
        } else {
            logger.log(Level.SEVERE, "clustering result is null!");
        }
    }

}
