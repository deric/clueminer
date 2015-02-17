package org.clueminer.explorer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionMO;
import org.clueminer.evolution.gui.EvolutionUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = EvolutionUI.class)
public class MoPanel extends JPanel implements EvolutionUI {

    private static final long serialVersionUID = -2664655185671435048L;
    private List<InternalEvaluator> internal;
    private JCheckBox[] boxes;
    private JSlider slGen;
    private JSlider slPop;
    private JTextField tfGen;
    private JTextField tfPop;

    public MoPanel() {
        initialize();
    }

    private void initialize() {
        setLayout(new GridBagLayout());
        initInternalEvaluator();

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.NONE;

        JLabel lbG = new JLabel("Generations: ");
        add(lbG, c);
        slGen = new JSlider();
        slGen.setMinimum(0);
        slGen.setMaximum(1000);
        slGen.setValue(20);
        slGen.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {
                if (tfGen != null) {
                    tfGen.setText(String.valueOf(slGen.getValue()));
                }
            }

        });
        c.gridx = 1;
        add(slGen, c);
        tfGen = new JTextField(slGen.getValue());
        tfGen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                int val = Integer.valueOf(tfGen.getText());
                slGen.setValue(val);
            }
        });
        c.gridx = 2;
        c.weightx = 0.1;
        add(tfGen, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.5;
        JLabel lbP = new JLabel("Population: ");
        add(lbP, c);
        slPop = new JSlider();
        slPop.setMinimum(0);
        slPop.setMaximum(1000);
        slPop.setValue(20);
        slPop.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {
                if (tfPop != null) {
                    tfPop.setText(String.valueOf(slPop.getValue()));
                }
            }
        });
        c.gridx = 1;
        c.gridy = 1;
        add(slPop, c);
        tfPop = new JTextField(slPop.getValue());
        tfPop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                int val = Integer.valueOf(tfPop.getText());
                slPop.setValue(val);
            }
        });
        c.gridx = 2;
        c.weightx = 0.1;
        add(tfPop, c);

        c.gridx = 0;
        c.weightx = 0.5;
        boxes = new JCheckBox[internal.size()];
        int i = 0;
        int col2 = boxes.length / 2;
        for (InternalEvaluator eval : internal) {
            boxes[i] = new JCheckBox(eval.getName());
            c.gridy = i + 2;
            if (i > col2) {
                c.gridx = 1;
                c.gridy -= col2 + 1;
            }
            add(boxes[i], c);
            i++;
        }

    }

    private void initInternalEvaluator() {
        InternalEvaluatorFactory ef = InternalEvaluatorFactory.getInstance();
        internal = ef.getAll();
    }

    @Override
    public void updateAlgorithm(Evolution alg) {
        alg.setGenerations(getGenerations());
        alg.setPopulationSize(getPopulation());
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i].isSelected()) {
                //TODO add objective functions
            }

        }
    }

    @Override
    public int getGenerations() {
        return slGen.getValue();
    }

    @Override
    public int getPopulation() {
        return slPop.getValue();
    }

    /**
     * Support only multi-objective algorithms
     *
     * @param evolve
     * @return
     */
    @Override
    public boolean isUIfor(Evolution evolve) {
        return evolve instanceof EvolutionMO;
    }

}
