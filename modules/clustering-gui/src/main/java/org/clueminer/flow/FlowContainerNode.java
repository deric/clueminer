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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.clueminer.flow.api.FlowNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author deric
 */
public class FlowContainerNode extends AbstractNode {

    private FlowNodeFactory factory;

    public FlowContainerNode(final FlowNodeFactory factory) {
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

    public FlowContainerNode(FlowNode node) {
        super(Children.LEAF, Lookups.singleton(node));
    }

    public FlowContainerNode(Children children) {
        super(children);
    }

    public FlowContainerNode(Children children, Lookup lookup) {
        super(children, lookup);
    }

    @Override
    public boolean canCut() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return getLookup().lookup(FlowNode.class).getName();
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public PasteType getDropType(final Transferable t, int arg1, int arg2) {

        if (t.isDataFlavorSupported(FlowFlavor.FLOW_FLAVOR)) {

            return new PasteType() {

                @Override
                public Transferable paste() throws IOException {
                    try {
                        factory.addNode((FlowNode) t.getTransferData(FlowFlavor.FLOW_FLAVOR));
                        final Node node = NodeTransfer.node(t, NodeTransfer.DND_MOVE + NodeTransfer.CLIPBOARD_CUT);
                        if (node != null) {
                            node.destroy();
                        }
                    } catch (UnsupportedFlavorException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    return null;
                }
            };
        } else {
            return null;
        }
    }

}
