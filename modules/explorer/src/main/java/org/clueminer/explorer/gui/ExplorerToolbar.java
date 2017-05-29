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
package org.clueminer.explorer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.ColorGeneratorFactory;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionFactory;
import org.clueminer.evolution.gui.EvolutionExport;
import org.clueminer.evolution.gui.EvolutionUI;
import org.clueminer.evolution.gui.EvolutionUIFactory;
import org.clueminer.explorer.ToolbarListener;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 */
public class ExplorerToolbar extends JToolBar {

    private static final long serialVersionUID = 2211255173237651641L;

    private JComboBox comboEvolution;
    private ToolbarListener listener;
    private JButton btnSingle;
    private JButton btnSettings;
    private JButton btnStart;
    private JButton btnFunction;
    private JButton btnExport;
    private JButton btnTrash;
    private EvalFuncPanel functionPanel;
    private ExportPanel exportPanel;
    private ClusterAlgPanel algPanel;
    private EvolutionUI evoPanel;
    private Evolution evolution;
    private Props props;
    private static final Logger LOG = LoggerFactory.getLogger(ExplorerToolbar.class);

    public ExplorerToolbar() {
        super(SwingConstants.HORIZONTAL);
        props = new Props();
        initComponents();
    }

    public void setListener(ToolbarListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        this.setFloatable(false);
        this.setRollover(true);
        btnSingle = new JButton(ImageUtilities.loadImageIcon("org/clueminer/explorer/clustering16.png", false));
        btnSingle.setToolTipText("Run single clustering");
        btnSingle.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (algPanel == null) {
                    algPanel = new ClusterAlgPanel();
                }
                if (listener != null) {
                    algPanel.setDataset(listener.getDataset());
                }
                DialogDescriptor dd = new DialogDescriptor(algPanel, NbBundle.getMessage(ExplorerToolbar.class, "AlgorithmPanel.title"));
                if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                    ClusteringAlgorithm alg = algPanel.getAlgorithm();
                    if (listener != null) {
                        Props p = algPanel.getProps();
                        int hash = p.hashCode();
                        if (functionPanel != null) {
                            functionPanel.updateProps(p);
                            if (p.containsKey("color_generator")) {
                                ColorGenerator cg = alg.getColorGenerator();
                                String color = (String) p.get(PropType.VISUAL, "color_generator");
                                if (color != null && !color.equals(cg.getName())) {
                                    LOG.debug("setting color generator {}", color);
                                    cg = ColorGeneratorFactory.getInstance().getProvider(color);
                                    alg.setColorGenerator(cg);
                                }
                            }
                        }
                        if (hash != p.hashCode()) {
                            listener.updateThumbnails(p);
                        }
                        listener.runClustering(alg, algPanel.getSelectedDataset(), p);
                    }
                }
            }
        });
        add(btnSingle);

        comboEvolution = new javax.swing.JComboBox();
        comboEvolution.setModel(new DefaultComboBoxModel(initEvolution()));

        comboEvolution.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (listener != null) {
                    evolution = EvolutionFactory.getInstance().getProvider(comboEvolution.getSelectedItem().toString());
                    evolution.setConfig(props);
                    listener.evolutionAlgorithmChanged(evt);
                }
            }
        });
        add(comboEvolution);
        addSeparator();

        btnSettings = new JButton(ImageUtilities.loadImageIcon("org/clueminer/explorer/settings16.png", false));
        btnSettings.setToolTipText("Setup evolution");
        btnSettings.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (evoPanel == null || !evoPanel.isUIfor(evolution)) {
                    EvolutionUIFactory factory = EvolutionUIFactory.getInstance();
                    for (EvolutionUI ui : factory.getAll()) {
                        if (ui.isUIfor(evolution)) {
                            evoPanel = ui;
                        }
                    }
                }
                DialogDescriptor dd = new DialogDescriptor(evoPanel, NbBundle.getMessage(ExplorerToolbar.class, "EvolutionPanel.title"));
                if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                    Evolution ev = getEvolution();
                    evoPanel.updateAlgorithm(ev);
                    ev.setConfig(props);
                    //start evolution right away
                    if (listener != null) {
                        listener.startEvolution(e, ev);
                    }
                }
            }
        });
        add(btnSettings);

        btnTrash = new JButton(ImageUtilities.loadImageIcon("org/clueminer/explorer/trash16.png", false));
        btnTrash.setToolTipText("Remove all clusterings");
        btnTrash.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (listener != null) {
                    listener.clearAll();
                }
            }
        });
        add(btnTrash);

        addSeparator();

        btnStart = new JButton("Start Clustering");

        btnStart.setFocusable(false);
        btnStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (listener != null) {
                    listener.startEvolution(evt, getEvolution());
                }

            }
        });
        add(btnStart);
        btnFunction = new JButton(ImageUtilities.loadImageIcon("org/clueminer/explorer/function16.png", false));
        btnFunction.setToolTipText("Choose evaluation function");
        btnFunction.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (functionPanel == null) {
                    functionPanel = new EvalFuncPanel(listener.getSortedClusterings());
                }
                DialogDescriptor dd = new DialogDescriptor(functionPanel, NbBundle.getMessage(ExplorerToolbar.class, "FunctionPanel.title"));
                if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                    if (listener.getComparator().equals(functionPanel.getComparator())) {

                    }
                    ClusterEvaluation eval = functionPanel.getEvaluator();
                    if (eval != null) {
                        if (listener != null) {
                            listener.evaluatorChanged(eval);
                        }
                    }
                    int hash = props.hashCode();
                    functionPanel.updateProps(props);

                    if (hash != props.hashCode()) {
                        listener.updateThumbnails(props);
                    }
                }
            }
        });
        add(btnFunction);
        btnExport = new JButton(ImageUtilities.loadImageIcon("org/clueminer/explorer/save16.png", false));
        btnExport.setToolTipText("Export results");
        btnExport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (exportPanel == null) {
                    exportPanel = new ExportPanel();
                }
                DialogDescriptor dd = new DialogDescriptor(exportPanel, NbBundle.getMessage(ExplorerToolbar.class, "ExplorerToolbar.title"));
                if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                    EvolutionExport exp = exportPanel.getExporter();
                    if (exp != null) {
                        if (listener != null) {
                            exp.setEvolution(listener.currentEvolution());
                            exp.export();
                        }
                    }
                }
            }
        });
        add(btnExport);
        addSeparator();
    }

    private Evolution getEvolution() {
        if (evolution == null) {
            evolution = EvolutionFactory.getInstance().getProvider(comboEvolution.getSelectedItem().toString());
        }
        return evolution;
    }

    private String[] initEvolution() {
        EvolutionFactory ef = EvolutionFactory.getInstance();
        List<String> list = ef.getProviders();
        String[] res = new String[list.size()];
        int i = 0;
        for (String s : list) {
            res[i++] = s;
        }
        return res;
    }

    public void evolutionStarted() {
        btnStart.setEnabled(false);
    }

    public void evolutionFinished() {
        btnStart.setEnabled(true);
    }

    public int getGenerations() {
        return evoPanel.getGenerations();
    }

}
