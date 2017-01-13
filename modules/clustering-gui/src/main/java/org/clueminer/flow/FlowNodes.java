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

import java.util.Collection;
import org.clueminer.flow.api.FlowNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class FlowNodes extends Children.SortedArray {

    private static final Logger LOG = LoggerFactory.getLogger(FlowNodes.class);

    public FlowNodes() {
        initialize();
    }

    private void initialize() {
        Collection<? extends FlowNode> res = Lookup.getDefault().lookupAll(FlowNode.class);
        LOG.info("found {} flow nodes", res.size());
        FlowNodeContainer[] cont = new FlowNodeContainer[res.size()];
        int i = 0;
        for (FlowNode fn : res) {
            cont[i++] = new FlowNodeContainer(fn);
        }
        add(cont);
    }

}
