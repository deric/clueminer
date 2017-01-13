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
package org.clueminer.flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.clueminer.flow.api.FlowNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory is responsible for creating drag&drop objects
 *
 * @author deric
 */
public class FlowNodeFactory extends ChildFactory<FlowNode> {

    List<FlowNode> nodes = new ArrayList<>();
    private static final Logger LOG = LoggerFactory.getLogger(FlowNodeFactory.class);

    public FlowNodeFactory() {
        //
    }

    public void addNode(FlowNode node) {
        nodes.add(node);
        refresh(true);
    }

    @Override
    protected boolean createKeys(List<FlowNode> toPopulate) {
        boolean ret = nodes.addAll(toPopulate);
        if (ret) {
            refresh(true);
        }
        return ret;
    }

    @Override
    protected Node createNodeForKey(final FlowNode fn) {
        //FlowNodeContainer node = new FlowNodeContainer(fn, this);
        //Node node = new FlowNodeContainer(Children.create(new FlowNodeFactory(fn.getName()), true), fn);
        Node node = new AbstractNode(Children.LEAF, Lookups.fixed(fn));
        node.setDisplayName(fn.getName());
        return node;
    }

    public void reorder(int[] perm) {
        FlowNode[] reordered = new FlowNode[nodes.size()];
        for (int i = 0; i < perm.length; i++) {
            int j = perm[i];

            FlowNode fn = nodes.get(i);
            reordered[j] = fn;
        }
        nodes.clear();
        nodes.addAll(Arrays.asList(reordered));
        refresh(true);

    }

}
