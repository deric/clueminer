/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Partitioning;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = Partitioning.class)
public class HMetisNoFF extends HMetis implements Partitioning {

    private static final String name = "hMETIS";

    @Override
    public String getName() {
        return name;
    }

    /**
     * Directly return hMetis result without flood fill
     *
     * @param maxPartitionSize
     * @param g
     * @return
     */
    @Override
    public ArrayList<LinkedList<Node>> partition(int maxPartitionSize, Graph g) {
        int k = (int) Math.ceil(g.getNodeCount() / (double) maxPartitionSize);
        if (k == 1) {
            ArrayList<LinkedList<Node>> nodes = new ArrayList<>();
            nodes.add(new LinkedList<>(g.getNodes().toCollection()));
            return nodes;
        }
        Node[] nodeMapping = createMapping(g);
        String path = runMetis(g, k);
        return importMetisResult(path, k, nodeMapping);
    }

}
