package org.clueminer.explorer;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.clueminer.clustering.api.evolution.EvolutionFactory;

/**
 *
 * @author Tomas Barton
 */
public class ExplorerToolbar extends JToolBar {

    private JComboBox comboEvolution;
    private javax.swing.JSlider sliderGenerations;
    private ToolbarListener listener;
    private JButton btnStart;

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
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (listener != null) {
                    listener.startEvolution(evt, (String) comboEvolution.getSelectedItem());
                }

            }
        });
        add(btnStart);

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
