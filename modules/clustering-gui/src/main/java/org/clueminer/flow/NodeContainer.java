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
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.flow.api.FlowNode;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.ProjectController;
import org.clueminer.utils.Props;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class NodeContainer extends Index.ArrayChildren {

    private static final Logger LOG = LoggerFactory.getLogger(NodeContainer.class);
    private List<Node> list = new ArrayList<Node>();
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);
    private ProjectController pc;

    public NodeContainer() {
        pc = Lookup.getDefault().lookup(ProjectController.class);
    }

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

    public void run() {
        //we can't start without active project
        if (pc.hasCurrentProject()) {
            final RequestProcessor.Task task = RP.create(new Runnable() {
                @Override
                public void run() {
                    LOG.info("started data-mining process");
                    Project project = pc.getCurrentProject();
                    Dataset<? extends Instance> dataset = project.getLookup().lookup(Dataset.class);
                    if (dataset != null) {
                        LOG.info("using dataset {}", dataset.getName());
                    } else {
                        LOG.error("missing dataset!");
                    }
                    LOG.info("process includes {} steps", list.size());
                    Props props = new Props();
                    Object[] inputs = new Object[]{dataset};
                    Object[] outputs;
                    for (Node node : list) {
                        FlowNode fn = node.getLookup().lookup(FlowNode.class);
                        LOG.info("applying {}, with {}", fn.getName(), fn.getProps());
                        outputs = fn.execute(inputs, fn.getProps());
                        inputs = outputs;
                    }
                    //
                }
            });

            task.addTaskListener(new TaskListener() {
                @Override
                public void taskFinished(Task task) {
                    LOG.info("data-mining finished.");

                }
            });
            task.schedule(0);
        } else {
            LOG.warn("missing current project");

        }
    }

}
