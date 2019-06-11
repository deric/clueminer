/*
 * Copyright (C) 2011-2019 clueminer.org
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
package org.clueminer.evaluation.inline;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.dataset.api.Instance;
import org.clueminer.export.api.ClusteringExport;
import org.clueminer.export.api.ClusteringExporterFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author deric
 */
public class SortingToolbar<E extends Instance> extends JToolBar {

    private JButton export;
    private JButton btnSettings;
    private final ScorePlot plot;
    private SettingsPanel settingsPanel;

    public SortingToolbar(ScorePlot plot) {
        super(SwingConstants.HORIZONTAL);
        this.plot = plot;
        initComponents();
    }

    private void initComponents() {
        this.setFloatable(false);
        this.setRollover(true);

        btnSettings = new JButton(ImageUtilities.loadImageIcon("org/clueminer/evaluation/gui/settings16.png", false));
        btnSettings.setToolTipText("Setup evolution");
        btnSettings.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (settingsPanel == null) {
                    settingsPanel = new SettingsPanel();
                }
                DialogDescriptor dd = new DialogDescriptor(settingsPanel, NbBundle.getMessage(SortingToolbar.class, "SortingToolbar.title"));
                if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                    settingsPanel.updatePlot(plot);
                    plot.resetCache();
                }
            }
        });
        add(btnSettings);

        export = new JButton(ImageUtilities.loadImageIcon("org/clueminer/evaluation/gui/save16.png", false));
        export.setToolTipText("Export current results");
        add(export);

        export.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                ClusteringExporterFactory cef = ClusteringExporterFactory.getInstance();
                ClusteringExport<E> exp = (ClusteringExport<E>) cef.getProvider("Export to CSV");
                exp.setDataset(plot.getDataset());
                //  exp.setResults(plot.getResults());
                exp.setClusterings(plot.getClusterings());
                exp.showDialog();
            }
        });
    }

    public void setEvaluatorX(ClusterEvaluation ex) {
        plot.setEvaluatorX(ex);
    }

}
