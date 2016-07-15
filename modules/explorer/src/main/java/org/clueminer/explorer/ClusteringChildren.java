/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.explorer;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.evolution.api.Evolution;
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
