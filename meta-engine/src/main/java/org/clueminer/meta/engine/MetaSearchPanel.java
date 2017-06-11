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
package org.clueminer.meta.engine;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.gui.EvolutionUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = EvolutionUI.class)
public class MetaSearchPanel extends JPanel implements EvolutionUI {

    private static final long serialVersionUID = -2664655185671435048L;
    private JSlider slGen;
    private JSlider slPop;
    private JTextField tfGen;
    private JTextField tfPop;
    private JTextField tfCrossover;
    private JComboBox<String> cbObj1;
    private JComboBox<String> cbObj2;
    private JComboBox<String> cbSort;

    public MetaSearchPanel() {
        initialize();
    }

    private void initialize() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 0.5;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.NONE;

        add(new JLabel("Pool size: "), c);
        tfPop = new JTextField("10");
        tfPop.setPreferredSize(new Dimension(50, 20));
        c.weightx = 1.0;
        c.gridx = 1;
        add(tfPop, c);

        //objectives
        InternalEvaluatorFactory ef = InternalEvaluatorFactory.getInstance();
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Objective 1: "), c);
        c.gridx = 1;
        cbObj1 = new JComboBox<>(ef.getProvidersArray());
        add(cbObj1, c);
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Objective 2: "), c);
        c.gridx = 1;
        cbObj2 = new JComboBox<>(ef.getProvidersArray());
        add(cbObj2, c);
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Sorting objective: "), c);
        c.gridx = 1;
        cbSort = new JComboBox<>(ef.getProvidersArray());
        add(cbSort, c);

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

    @Override
    public void updateAlgorithm(Evolution alg) {
        MetaSearch meta = (MetaSearch) alg;
        alg.setGenerations(getGenerations());
        alg.setPopulationSize(getPopulation());
        meta.clearObjectives();
        InternalEvaluatorFactory ef = InternalEvaluatorFactory.getInstance();
        meta.addObjective((ClusterEvaluation) ef.getProvider((String) cbObj1.getSelectedItem()));
        meta.addObjective((ClusterEvaluation) ef.getProvider((String) cbObj2.getSelectedItem()));
        meta.setSortObjective((ClusterEvaluation) ef.getProvider((String) cbSort.getSelectedItem()));

        //meta.setGenerations(getGenerations());
        meta.setPopulationSize(getPopulation());
        meta.setCrossoverProbability(getCrossover());
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
        return evolve instanceof MetaSearch;
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
