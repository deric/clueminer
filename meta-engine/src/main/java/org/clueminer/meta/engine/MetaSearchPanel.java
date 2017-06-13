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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
    private JTextField tfFronts;
    private JTextField tfResults;
    private JTextField tfNumStates;
    private JComboBox<String> cbObj1;
    private JComboBox<String> cbObj2;
    private JComboBox<String> cbSort;
    private JCheckBox chckUseMetaDB;
    private JCheckBox chckLimitSolutions;
    private JCheckBox chckExpandOnlyTop;

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

        add(new JLabel("Number of resuts: "), c);
        tfResults = new JTextField("10");
        tfResults.setPreferredSize(new Dimension(50, 22));
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 1;
        add(tfResults, c);

        c.gridx = 0;
        c.gridy++;
        add(new JLabel("Number of fronts: "), c);
        tfFronts = new JTextField("10");
        tfFronts.setPreferredSize(new Dimension(50, 22));
        c.weightx = 1.0;
        c.gridx = 1;
        add(tfFronts, c);

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
        cbObj2.setSelectedItem("Ratkowsky-Lance");
        add(cbObj2, c);
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Sorting objective: "), c);
        c.gridx = 1;
        cbSort = new JComboBox<>(ef.getProvidersArray());
        cbSort.setSelectedItem("McClain-Rao");
        add(cbSort, c);

        c.gridx = 0;
        c.gridy++;
        chckUseMetaDB = new JCheckBox("Use Meta DB", false);
        c.weightx = 1.0;
        c.gridwidth = 2;
        add(chckUseMetaDB, c);

        c.gridx = 0;
        c.gridy++;
        chckLimitSolutions = new JCheckBox("Limit explored states", true);
        c.weightx = 1.0;
        c.gridwidth = 1;
        add(chckLimitSolutions, c);
        tfNumStates = new JTextField("100");
        tfNumStates.setPreferredSize(new Dimension(50, 22));
        c.gridx = 1;
        add(tfNumStates, c);

        c.gridx = 0;
        c.gridy++;
        chckExpandOnlyTop = new JCheckBox("Expand only top solutions", false);
        c.weightx = 1.0;
        c.gridwidth = 2;
        add(chckExpandOnlyTop, c);

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
        meta.setNumResults(Integer.parseInt(tfResults.getText()));
        meta.setUseMetaDB(chckUseMetaDB.isSelected());
        meta.setMaxSolutions(Integer.parseInt(tfNumStates.getText()));
        meta.setExpandOnlyTop(chckExpandOnlyTop.isSelected());

        //meta.setGenerations(getGenerations());
        meta.setPopulationSize(Integer.parseInt(tfFronts.getText()));
    }

    public double getCrossover() {
        return 0.0;
    }

    @Override
    public int getGenerations() {
        return 1;
    }

    @Override
    public int getPopulation() {
        return 10;
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

}
