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
package org.clueminer.graph.impl;

import java.util.HashSet;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.Node;

/**
 *
 * @author deric
 */
public abstract class AbstractGraphBuilder<E extends Instance> implements GraphBuilder<E> {

    protected static long nodeIdCounter = 0;
    protected static long edgeIdCounter = 0;

    @Override
    public Long[] createNodesFromInput(Dataset<E> input, Graph<E> graph) {
        nodeIdCounter = 0;
        edgeIdCounter = 0;
        Long[] mapping = new Long[input.size()];
        for (E inst : input) {
            Node node = this.newNode();
            mapping[inst.getIndex()] = node.getId();
            node.setInstance(inst);
            graph.addNode(node);
        }
        return mapping;
    }

    /**
     * Create mapping without instances marked as noise.
     *
     * @param input
     * @param graph
     * @param noise
     * @return
     */
    @Override
    public Long[] createNodesFromInput(Dataset<E> input, Graph<E> graph, HashSet<Integer> noise) {
        if (noise == null || noise.isEmpty()) {
            return createNodesFromInput(input, graph);
        }

        nodeIdCounter = 0;
        edgeIdCounter = 0;
        Long[] mapping = new Long[input.size()];

        for (E inst : input) {
            if (!noise.contains(inst.getIndex())) {
                Node node = this.newNode();
                //k-NN uses index in dataset (we can't skip noise)
                mapping[inst.getIndex()] = node.getId();
                node.setInstance(inst);
                graph.addNode(node);
            }
        }
        return mapping;
    }
}
