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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.clueminer.flow.api.FlowFlavor;
import org.clueminer.flow.api.FlowNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.lookup.Lookups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container is used to visually represent FlowNodes
 *
 * @author deric
 */
public class FlowNodeContainer extends AbstractNode implements Transferable {

    private static final Logger LOG = LoggerFactory.getLogger(FlowNodeContainer.class);
    private final DataFlavor[] flavors = new DataFlavor[]{FlowFlavor.FLOW_NODE};

    public FlowNodeContainer(FlowNode fn) {
        super(Children.LEAF, Lookups.singleton(fn));
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
    public Transferable clipboardCopy() throws IOException {
        Transferable deflt = super.clipboardCopy();
        ExTransferable added = ExTransferable.create(deflt);
        added.put(new ExTransferable.Single(FlowFlavor.FLOW_NODE) {
            @Override
            protected FlowNode getData() {
                return getLookup().lookup(FlowNode.class);
            }
        });
        return added;
    }

    /*
     * @Override
     * public PasteType getDropType(final Transferable t, int arg1, int arg2) {
     * LOG.info("getting drop type {}", t);
     *
     * if (t.isDataFlavorSupported(FlowFlavor.FLOW_NODE)) {
     *
     * return new PasteType() {
     *
     * @Override
     * public Transferable paste() throws IOException {
     * try {
     * LOG.info("adding node");
     * factory.addNode((FlowNode) t.getTransferData(FlowFlavor.FLOW_NODE));
     * final Node node = NodeTransfer.node(t, NodeTransfer.DND_MOVE + NodeTransfer.CLIPBOARD_CUT);
     * if (node != null) {
     * node.destroy();
     * }
     * } catch (UnsupportedFlavorException ex) {
     * Exceptions.printStackTrace(ex);
     * }
     * return null;
     * }
     * };
     * } else {
     * return null;
     * }
     * } */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(FlowFlavor.FLOW_NODE);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return getLookup().lookup(FlowNode.class);
    }

}
