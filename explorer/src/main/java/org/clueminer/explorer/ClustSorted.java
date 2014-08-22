package org.clueminer.explorer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.evolution.Evolution;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Tomas Barton
 */
public class ClustSorted extends Children.SortedArray {

    private Lookup.Result<Clustering> result;
    private static final Logger logger = Logger.getLogger(ClustGlobal.class.getName());
    private Set<Clustering> all = new HashSet<Clustering>(5);

    public ClustSorted() {

    }

    public ClustSorted(Evolution alg) {
        result = alg.getLookup().lookupResult(Clustering.class);
        result.addLookupListener(new LookupListener() {
            @Override
            public void resultChanged(LookupEvent evt) {
                logger.log(Level.INFO, "clust child lookup event! {0}", evt);
                addNotify();
            }
        });

    }

    public ClustSorted(Lookup.Result<Clustering> result) {
        this.result = result;
        this.result.addLookupListener(new LookupListener() {
            @Override
            public void resultChanged(LookupEvent evt) {
                //logger.log(Level.INFO, "clust child lookup event! {0}", evt);
                addNotify();
            }
        });

    }

    @Override
    protected void addNotify() {
        if (result != null) {
            Collection<? extends Clustering> coll = result.allInstances();
            if (coll != null && coll.size() > 0) {
                all.addAll(coll);
                ClusteringNode[] nodesAry = new ClusteringNode[coll.size()];
                int i = 0;
                for (Clustering c : coll) {
                    nodesAry[i++] = new ClusteringNode(c);
                }
                add(nodesAry);
                //setKeys(all);
            }
        } else {
            logger.log(Level.SEVERE, "clustering result is null!");
        }
    }

}
