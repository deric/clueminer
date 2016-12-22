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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.openide.nodes.Index;
import org.openide.nodes.Node;

/**
 *
 * @author deric
 */
public class NodeContainer extends Index.ArrayChildren {

    private List<Node> list = new ArrayList<>();


    @Override
    protected List<Node> initCollection() {
        return list;
    }

    public ListIterator<FlowNodes> getRemaining(Node current) {
        List<FlowNodes> v = new ArrayList<>();
        for (Node n : list.subList(indexOf(current), list.size())) {
            v.add(n.getLookup().lookup(FlowNodes.class));
        }
        return v.listIterator();
    }

    public void add(Node n) {
        add(new Node[]{n});
    }

}
