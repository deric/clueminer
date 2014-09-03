package org.clueminer.explorer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.clustering.api.evolution.EvolutionFactory;
import org.clueminer.explorer.ToolbarListener;
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

    private JComboBox comboEvolution;
    private javax.swing.JSlider sliderGenerations;
    private ToolbarListener listener;
    private JButton btnStart;
    private JButton btnFunction;
    private EvalFuncPanel functionPanel;

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

        comboEvolution = new javax.swing.JComboBox();
        comboEvolution.setModel(new DefaultComboBoxModel(initEvolution()));

        comboEvolution.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (listener != null) {
                    listener.evolutionAlgorithmChanged(evt);
                }
            }
        });

        add(comboEvolution);
        addSeparator();
        add(new JLabel("generations:"));

        sliderGenerations = new JSlider(SwingConstants.HORIZONTAL);
        sliderGenerations.setMaximum(200);
        sliderGenerations.setMinimum(10);
        sliderGenerations.setValue(10);
        add(sliderGenerations);

        btnStart = new JButton("Start Clustering");

        btnStart.setFocusable(false);
        btnStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (listener != null) {
                    listener.startEvolution(evt, (String) comboEvolution.getSelectedItem());
                }

            }
        });
        add(btnStart);
        btnFunction = new JButton(ImageUtilities.loadImageIcon("org/clueminer/clueminer/explorer/function16.png", false));
        btnFunction.setToolTipText("Choose evaluation function");
        btnFunction.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (functionPanel == null) {
                    functionPanel = new EvalFuncPanel();
                }
                DialogDescriptor dd = new DialogDescriptor(functionPanel, NbBundle.getMessage(ExplorerToolbar.class, "FunctionPanel.title"));
                if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                    ExternalEvaluator eval = functionPanel.getEvaluator();
                    if (eval != null) {
                        if (listener != null) {
                            listener.evaluatorChanged(eval);
                        }
                    }
                }
            }
        });
        add(btnFunction);
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
        return sliderGenerations.getValue();
    }

}
