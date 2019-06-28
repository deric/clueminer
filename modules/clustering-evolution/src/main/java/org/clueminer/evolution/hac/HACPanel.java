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
package org.clueminer.evolution.hac;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.gui.EvolutionUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = EvolutionUI.class)
public class HACPanel extends JPanel implements EvolutionUI {

    private static final long serialVersionUID = -2664655185671435048L;
    private JTextField tfNumStates;
    private JCheckBox chckDistance;
    private JCheckBox chckLimitSolutions;
    private static final String NAME = "HAC";

    public HACPanel() {
        initialize();
    }

    @Override
    public String getName() {
        return NAME;
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

        c.gridx = 0;
        c.gridy++;
        chckDistance = new JCheckBox("Modify distance function", false);
        c.weightx = 1.0;
        c.gridwidth = 2;
        add(chckDistance, c);

        c.gridx = 0;
        c.gridy++;
        chckLimitSolutions = new JCheckBox("Limit explored states", true);
        c.weightx = 1.0;
        c.gridwidth = 1;
        add(chckLimitSolutions, c);
        tfNumStates = new JTextField("1000");
        tfNumStates.setPreferredSize(new Dimension(50, 22));
        c.gridx = 1;
        add(tfNumStates, c);

    }

    @Override
    public void updateAlgorithm(Evolution alg) {
        BruteForceHacEvolution hac = (BruteForceHacEvolution) alg;

        hac.setModifyDistance(chckDistance.isSelected());
        if (chckLimitSolutions.isSelected()) {
            hac.setLimitStates(Integer.parseInt(tfNumStates.getText()));
        } else {
            hac.setLimitStates(-1);
        }
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
        return evolve instanceof BruteForceHacEvolution;
    }

}
