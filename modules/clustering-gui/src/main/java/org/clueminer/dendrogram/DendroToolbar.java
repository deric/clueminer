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
package org.clueminer.dendrogram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.clustering.gui.ClusteringExport;
import org.clueminer.utils.ImageExporter;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;

/**
 *
 * @author Tomas Barton
 */
public class DendroToolbar extends JToolBar {

    private static final long serialVersionUID = 3796559248116111100L;
    private JButton btnFitToSpace;
    private JButton btnScreenshot;
    private JButton btnExport;
    private JToggleButton btnEvaluation;
    private JToggleButton btnLegend;
    private final DendroViewer viewer;

    public DendroToolbar(DendroViewer viewer) {
        super(SwingConstants.HORIZONTAL);
        this.viewer = viewer;
        initComponents();
    }

    private void initComponents() {

        this.setFloatable(false);
        this.setRollover(true);

        btnFitToSpace = new JButton(ImageUtilities.loadImageIcon("org/clueminer/dendrogram/gui/fullscreen16.png", false));
        btnFitToSpace.setToolTipText("Fit to window");
        btnFitToSpace.setSelected(true);
        btnScreenshot = new JButton(ImageUtilities.loadImageIcon("org/clueminer/dendrogram/gui/screenshot16.png", false));
        btnScreenshot.setToolTipText("Make a screenshot of this window");
        btnExport = new JButton(ImageUtilities.loadImageIcon("org/clueminer/dendrogram/gui/save16.png", false));
        btnExport.setToolTipText("Export this dendrogram");
        btnEvaluation = new JToggleButton(ImageUtilities.loadImageIcon("org/clueminer/dendrogram/gui/eval16.png", false));
        btnLegend = new JToggleButton(ImageUtilities.loadImageIcon("org/clueminer/dendrogram/gui/legend16.png", false));
        btnLegend.setSelected(true);
        add(btnFitToSpace);
        add(btnScreenshot);
        add(btnExport);
        add(btnEvaluation);
        add(btnLegend);
        addSeparator();

        btnFitToSpace.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                viewer.setFitToPanel(btnFitToSpace.isSelected());
            }
        });

        btnScreenshot.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ImageExportDialog exportDialog = new ImageExportDialog();
                DialogDescriptor dd = new DialogDescriptor(exportDialog, "Screenshot");
                if (!DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                    //exportDialog.destroy();
                    return;
                }
                //exportDialog.destroy();

                if (dd.getValue() == DialogDescriptor.OK_OPTION) {
                    ImageExporter exp = (ImageExporter) Utilities.actionsGlobalContext().lookupResult(ImageExporter.class);
                    exp.export(viewer);
                }

            }
        });

        btnExport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FileExportDialog exportDialog = new FileExportDialog();
                DialogDescriptor dd = new DialogDescriptor(exportDialog, "Export to...");
                if (!DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                    //exportDialog.destroy();
                    return;
                }
                //exportDialog.destroy();

                if (dd.getValue() == DialogDescriptor.OK_OPTION) {
                    ClusteringExport exp = exportDialog.getExporter();
                    exp.setViewer(viewer);
                    exp.export();
                }
            }
        });

        btnEvaluation.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                viewer.setEvaluationVisible(!viewer.isEvaluationVisible());
            }
        });

        btnLegend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewer.setLegendVisible(!viewer.isLegendVisible());
            }
        });

    }

}
