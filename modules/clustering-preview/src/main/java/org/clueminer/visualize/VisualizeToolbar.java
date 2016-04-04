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
package org.clueminer.visualize;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.clueminer.export.impl.ImageExporter;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;

/**
 *
 * @author deric
 */
public class VisualizeToolbar extends JToolBar {

    private JButton btnExport;
    private JButton btnScreenshot;
    private final VisualizePanel viewer;

    public VisualizeToolbar(VisualizePanel frame) {
        super(SwingConstants.HORIZONTAL);
        this.viewer = frame;
        initialize();
    }

    private void initialize() {
        this.setFloatable(false);
        this.setRollover(true);

        btnExport = new JButton(ImageUtilities.loadImageIcon("org/clueminer/visualize/save16.png", false));
        btnExport.setToolTipText("Export clustering");
        btnScreenshot = new JButton(ImageUtilities.loadImageIcon("org/clueminer/visualize/screenshot16.png", false));
        btnScreenshot.setToolTipText("Make a screenshot of this window");
        add(btnScreenshot);
        add(btnExport);
        addSeparator();

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
                    ImageExporter.getDefault().export(viewer);
                }

            }
        });

    }

}
