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
package org.clueminer.flow;

import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.clueminer.flow.api.FlowNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 * Factory is responsible for creating drag&drop objects
 *
 * @author deric
 */
public class FlowNodeFactory<T extends FlowNode> extends ChildFactory<T> {

    List<T> nodes = new ArrayList<>();

    public void addNode(T node) {
        nodes.add(node);
        refresh(true);
    }

    @Override
    protected boolean createKeys(List<T> toPopulate) {
        boolean ret = nodes.addAll(toPopulate);
        if (ret) {
            refresh(true);
        }
        return ret;
    }

    public void reorder(int[] perm) {
        FlowNode[] reordered = new FlowNode[nodes.size()];
        for (int i = 0; i < perm.length; i++) {
            int j = perm[i];

            FlowNode c = nodes.get(i);
            reordered[j] = c;
        }
        nodes.clear();
        nodes.addAll((Collection<? extends T>) Arrays.asList(reordered));
        refresh(true);

    }

    @Override
    protected Node createNodeForKey(final FlowNode fn) {
        Node node = new FlowContainerNode(Children.LEAF) {

            @Override
            public Transferable drag() throws IOException {
                return fn;
            }

        };
        node.setDisplayName(fn.getName());
        return node;
    }

}
