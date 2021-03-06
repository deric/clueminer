/*
 * Copyright (C) 2011-2018 clueminer.org
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
import java.util.HashSet;
import java.util.Set;
import org.clueminer.clustering.api.Clustering;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 */
public class ClustGlobal extends Children.Keys<Clustering> {

    private Lookup.Result<Clustering> result;
    private static final Logger LOG = LoggerFactory.getLogger(ClustGlobal.class);
    private Set<Clustering> all = new HashSet<>(5);

    public ClustGlobal() {

    }

    public ClustGlobal(Lookup.Result<Clustering> result) {
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
            LOG.error("clustering result is null!");
        }
    }

}
