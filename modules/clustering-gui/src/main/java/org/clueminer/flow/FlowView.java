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

import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionListener;
import org.clueminer.flow.api.FlowFlavor;
import org.clueminer.flow.api.FlowNode;
import org.openide.explorer.view.TreeTableView;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class FlowView extends TreeTableView {

    private static final Logger LOG = LoggerFactory.getLogger(FlowView.class);
    private NodeContainer container;

    public FlowView(NodeContainer container) {
        setRootVisible(false);
        this.container = container;
        setDropTarget();
    }

    public void setDefaultActionProcessor(final ActionListener action) {
        setDefaultActionAllowed(false);
    }

    private void setDropTarget() {
        DropTarget dt = new DropTarget(this, new DropTargetAdapter() {

            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                if (!dtde.isDataFlavorSupported(FlowFlavor.FLOW_NODE)) {
                    dtde.rejectDrag();
                }
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    FlowNode flow = (FlowNode) dtde.getTransferable().getTransferData(FlowFlavor.FLOW_NODE);
                    FlowNodeContainer dataNode = new FlowNodeContainer(flow, container);
                    //ExplorerManager.find(getParent()).getRootContext().getChildren().add(new Node[]{dataNode});
                    container.add(dataNode);
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                    dtde.rejectDrop();
                }
            }

        });

        setDropTarget(dt);
    }

    public NodeContainer getContainer() {
        return container;
    }


}
