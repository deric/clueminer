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

import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.flow.api.FlowError;
import org.clueminer.flow.api.FlowNode;
import org.clueminer.gui.msg.NotifyUtil;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.ProjectController;
import org.openide.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class FlowExecutor implements Runnable {

    private ProjectController pc;
    private final Node[] list;
    private FlowToolbar toolbar;
    private static final Logger LOG = LoggerFactory.getLogger(FlowExecutor.class);

    public FlowExecutor(FlowToolbar toolbar, ProjectController pc, final Node[] list) {
        this.pc = pc;
        this.list = list;
        this.toolbar = toolbar;
    }

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
        LOG.info("process includes {} steps", list.length);
        Object[] inputs = new Object[]{dataset};
        Object[] outputs = new Object[0];
        String currFlow = null;
        try {
            for (Node node : list) {
                FlowNode fn = node.getLookup().lookup(FlowNode.class);
                currFlow = fn.getName();
                LOG.info("applying {}, with {}", fn.getName(), fn.getProps());
                outputs = fn.execute(inputs, fn.getProps());
                if (outputs.length > 0) {
                    LOG.debug("output 0 is {}", outputs[0].getClass());
                }

                inputs = outputs;
            }
            //process result - check whether there's some clustering
            for (Object o : outputs) {
                if (o instanceof Clustering) {
                    Clustering c = (Clustering) o;
                    pc.getCurrentProject().add(c);
                }
            }
        } catch (FlowError e) {
            LOG.error("flow {} failed", currFlow, e);
            NotifyUtil.error("Flow Error", e.getMessage(), e, true);
            toolbar.taskFinished();
        }
    }

}
