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

import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.clueminer.gui.msg.NotifyUtil;
import org.clueminer.project.api.ProjectController;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
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
public class FlowToolbar extends JToolBar {

    private static final long serialVersionUID = 4815833013275855141L;

    private JButton btnRun;
    private JButton btnSave;
    private JButton btnLoad;
    private final NodeContainer container;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);
    private final ProjectController pc;
    private static final Logger LOG = LoggerFactory.getLogger(FlowToolbar.class);


    public FlowToolbar(NodeContainer container) {
        super(SwingConstants.HORIZONTAL);
        this.container = container;
        pc = Lookup.getDefault().lookup(ProjectController.class);
        initComponents();
    }

    private void initComponents() {
        this.setFloatable(false);
        this.setRollover(true);

        btnRun = new JButton(ImageUtilities.loadImageIcon("org/clueminer/flow/play16.png", false));
        btnRun.setToolTipText("Execute flow");

        btnRun.addActionListener((ActionEvent e) -> {
            btnRun.setEnabled(false);
            run(container.getNodes().clone());
        });

        add(btnRun);
        addSeparator();

        btnLoad = new JButton(ImageUtilities.loadImageIcon("org/clueminer/flow/open16.png", false));
        btnLoad.setToolTipText("Load flow");

        btnLoad.addActionListener((ActionEvent e) -> {
            btnLoad.setEnabled(false);
            //
        });
        add(btnLoad);

        btnSave = new JButton(ImageUtilities.loadImageIcon("org/clueminer/flow/save16.png", false));
        btnSave.setToolTipText("Save flow");

        btnSave.addActionListener(new FlowExporter(container));
        add(btnSave);

    }

    /**
     * Run data-mining job
     *
     * @param list
     */
    private void run(final Node[] list) {
        //we can't start without active project
        if (pc.hasCurrentProject()) {
            final RequestProcessor.Task task = RP.create(new FlowExecutor(this, pc, list));

            task.addTaskListener(new TaskListener() {
                @Override
                public void taskFinished(Task task) {
                    LOG.info("data-mining finished.");
                    btnRun.setEnabled(true);
                }
            });
            task.schedule(0);
        } else {
            LOG.warn("missing current project");
            NotifyUtil.error("Flow Warning", "missing current project", true);
        }
    }

    public void taskFinished() {
        btnRun.setEnabled(true);
    }

}
