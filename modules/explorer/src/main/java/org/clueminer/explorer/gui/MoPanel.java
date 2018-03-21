/*
 * Copyright (C) 2011-2018 clueminer.org
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    private JTextField tfCrossover;

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
        slGen.addChangeListener(new GenerationUpdater());
        c.gridx = 1;
        add(slGen, c);
        tfGen = new JTextField(slGen.getValue());
        tfGen.getDocument().addDocumentListener(new GenerationListener());
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
        slPop.addChangeListener(new PopulationUpdater());
        c.gridx = 1;
        c.gridy = 1;
        add(slPop, c);
        tfPop = new JTextField(slPop.getValue());
        tfPop.getDocument().addDocumentListener(new PopulationListener());
        c.gridx = 2;
        c.weightx = 0.1;
        add(tfPop, c);

        c.gridy = 2;
        c.gridx = 0;
        JLabel lb = new JLabel("Crossover prob.: ");
        add(lb, c);
        c.gridx = 1;
        tfCrossover = new JTextField("0.9");
        add(tfCrossover, c);

        c.gridx = 0;
        c.weightx = 0.5;
        boxes = new JCheckBox[internal.size()];
        int i = 0;
        int col2 = boxes.length / 2;
        for (InternalEvaluator eval : internal) {
            boxes[i] = new JCheckBox(eval.getName());
            c.gridy = i + 3;
            if (i > col2) {
                c.gridx = 1;
                c.gridy -= col2 + 1;
            }
            add(boxes[i], c);
            i++;
        }

    }

    private void parseGeneration() {
        try {
            int val = Integer.valueOf(tfGen.getText());
            slGen.setValue(val);
        } catch (NumberFormatException e) {
            //can't parse number
        }
    }

    private void parsePopulation() {
        try {
            int val = Integer.valueOf(tfPop.getText());
            slPop.setValue(val);
        } catch (NumberFormatException e) {
            //can't parse number
        }
    }

    private void initInternalEvaluator() {
        InternalEvaluatorFactory ef = InternalEvaluatorFactory.getInstance();
        internal = ef.getAll();
    }

    @Override
    public void updateAlgorithm(Evolution alg) {
        EvolutionMO moAlg = (EvolutionMO) alg;
        alg.setGenerations(getGenerations());
        alg.setPopulationSize(getPopulation());
        moAlg.clearObjectives();
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i].isSelected()) {
                //TODO add objective functions
                moAlg.addObjective(internal.get(i));
            }
        }
        moAlg.setGenerations(getGenerations());
        moAlg.setPopulationSize(getPopulation());
        moAlg.setCrossoverProbability(getCrossover());
    }

    public double getCrossover() {
        double cross = Double.parseDouble(tfCrossover.getText());
        if (Double.isNaN(cross) | cross < 0.0) {
            cross = 0.0;
        }
        return cross;
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

    private class PopulationListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent de) {
            parsePopulation();
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            parsePopulation();
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            parsePopulation();
        }
    }

    private class GenerationListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent de) {
            parseGeneration();
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            parseGeneration();
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            parseGeneration();
        }
    }

    private class PopulationUpdater implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent ce) {
            if (tfPop != null && ce.getSource() != tfPop) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        tfPop.setText(String.valueOf(slPop.getValue()));
                    }
                });
            }
        }
    }

    private class GenerationUpdater implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent ce) {
            if (tfPop != null && ce.getSource() != tfPop) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        tfGen.setText(String.valueOf(slGen.getValue()));
                    }
                });
            }
        }
    }

}
