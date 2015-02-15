package org.clueminer.explorer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.explorer.EvolutionUI;

/**
 *
 * @author Tomas Barton
 */
public class MoPanel extends JPanel implements EvolutionUI {

    private static final long serialVersionUID = -2664655185671435048L;
    private List<InternalEvaluator> internal;
    private JCheckBox[] boxes;

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
        c.weightx = 1;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.NONE;

        boxes = new JCheckBox[internal.size()];
        int i = 0;
        int col2 = boxes.length / 2;
        for (InternalEvaluator eval : internal) {
            boxes[i] = new JCheckBox(eval.getName());
            c.gridy = i;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getPopulation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
