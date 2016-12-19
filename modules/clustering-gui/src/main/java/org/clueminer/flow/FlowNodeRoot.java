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
import java.util.Arrays;
import org.clueminer.flow.api.FlowFlavor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.datatransfer.PasteType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Root node of the execution flow.
 *
 * @author deric
 */
public class FlowNodeRoot extends AbstractNode {

    private static final Logger LOG = LoggerFactory.getLogger(FlowNodeRoot.class);
    private FlowNodeFactory factory;

    public FlowNodeRoot(final FlowNodeFactory factory) {
        super(Children.create(factory, true));
        this.factory = factory;
        getCookieSet().add(new Index.Support() {

            @Override
            public Node[] getNodes() {
                return getChildren().getNodes();
            }

            @Override
            public int getNodesCount() {
                return getNodes().length;
            }

            @Override
            public void reorder(int[] perm) {
                factory.reorder(perm);
            }
        });
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public PasteType getDropType(final Transferable t, int arg1, int arg2) {
        LOG.info("root: got drop {}", Arrays.toString(t.getTransferDataFlavors()));
        if (t.isDataFlavorSupported(FlowFlavor.FLOW_NODE)) {

            return new PasteType() {

                @Override
                public Transferable paste() throws IOException {
                    LOG.info("adding node");
                    final Node node = NodeTransfer.node(t, NodeTransfer.DND_COPY + NodeTransfer.CLIPBOARD_COPY);
                    LOG.info("transferred node {}", node);
                    /* if (node != null) {
                     * factory.addNode((FlowNode) node);
                     * } */
                    //factory.addNode((FlowNode) t.getTransferData(FlowFlavor.FLOW_NODE));
                    return null;
                }
            };
        } else {
            LOG.info("flavor not supported {}", Arrays.toString(t.getTransferDataFlavors()));
            return null;
        }
    }

}
