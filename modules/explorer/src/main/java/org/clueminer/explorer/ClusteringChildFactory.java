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
package org.clueminer.explorer;

import java.util.Arrays;
import java.util.List;
import org.clueminer.clustering.struct.ClusterList;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringChildFactory extends ChildFactory<ClusteringNode> {

    @Override
    protected boolean createKeys(List<ClusteringNode> toPopulate) {
        ClusteringNode[] objs = new ClusteringNode[5];
        for (int i = 0; i < objs.length; i++) {
            objs[i] = new ClusteringNode(new ClusterList(5));
        }
        toPopulate.addAll(Arrays.asList(objs));
        return true;
    }

    @Override
    protected Node createNodeForKey(ClusteringNode key) {
        Node result = new AbstractNode(Children.create(new ClusteringChildFactory(), true), Lookups.singleton(key));
        result.setDisplayName(key.toString());
        return result;
    }

}
