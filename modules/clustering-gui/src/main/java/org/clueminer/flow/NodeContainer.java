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

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import org.openide.nodes.Index;
import org.openide.nodes.Node;

/**
 *
 * @author deric
 */
public class NodeContainer extends Index.ArrayChildren implements FlowNodeModel {

    private static final long serialVersionUID = 1747942885452246170L;

    private final List<Node> list = new ArrayList<>();


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

    @Override
    public boolean add(FlowNodeContainer node) {
        return add(new Node[]{node});
    }

    @Override
    public boolean remove(FlowNodeContainer node) {
        return remove(new Node[]{node});
    }

    @Override
    public String serialize() {
        Gson gson = new Gson();
        HashMap<String, String> data = new HashMap<>();
        data.put("version", "1.0");

        gson.toJson(this);

        return gson.toJson(data);
    }

}
