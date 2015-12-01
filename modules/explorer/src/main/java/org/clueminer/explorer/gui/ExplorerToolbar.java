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
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionFactory;
import org.clueminer.evolution.gui.EvolutionExport;
import org.clueminer.evolution.gui.EvolutionUI;
import org.clueminer.evolution.gui.EvolutionUIFactory;
import org.clueminer.explorer.ToolbarListener;
import org.clueminer.utils.Props;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

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

    public ExplorerToolbar() {
        super(SwingConstants.HORIZONTAL);
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
                    functionPanel = new EvalFuncPanel();
                }
                DialogDescriptor dd = new DialogDescriptor(functionPanel, NbBundle.getMessage(ExplorerToolbar.class, "FunctionPanel.title"));
                if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                    ClusterEvaluation eval = functionPanel.getEvaluator();
                    if (eval != null) {
                        if (listener != null) {
                            listener.evaluatorChanged(eval);
                        }
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
