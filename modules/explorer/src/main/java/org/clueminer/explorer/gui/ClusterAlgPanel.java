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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.clustering.gui.ClusteringDialog;
import org.clueminer.clustering.gui.ClusteringDialogFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class ClusterAlgPanel<E extends Instance> extends JPanel {

    private static final long serialVersionUID = 3764607764760405449L;

    private JComboBox<String> cbType;
    private JComboBox<String> cbData;
    private JPanel optPanel;
    private String selected = null;
    private ClusteringDialog dialog = null;
    private Dataset<E> dataset;
    private Dataset<E> currDataset;

    public ClusterAlgPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        ClusteringFactory factory = ClusteringFactory.getInstance();
        cbType = new JComboBox<>(factory.getProvidersArray());
        if (selected != null) {
            cbType.setSelectedItem(selected);
        }
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 0.3;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.NONE;
        add(cbType, c);
        cbType.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selected = (String) cbType.getSelectedItem();
                removeAll();
                initComponents();
                updateCheckbox(dataset);
                repaint();
                revalidate();
            }
        });
        c.gridx++;
        add(new JLabel("Dataset: "), c);
        c.gridx++;
        cbData = new JComboBox<>();
        add(cbData, c);
        cbData.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String curr = (String) cbData.getSelectedItem();
                if (curr.equals(dataset.getName())) {
                    currDataset = dataset;
                } else {
                    currDataset = dataset.getChild(curr);
                }
                System.out.println("curr is: " + currDataset.getName());
            }

        });

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        String expName = (String) cbType.getSelectedItem();
        ClusteringAlgorithm alg = factory.getProvider(expName);
        optPanel = getUI(alg);
        add(optPanel, c);
    }

    private JPanel getUI(ClusteringAlgorithm alg) {
        for (ClusteringDialog dlg : ClusteringDialogFactory.getInstance().getAll()) {
            if (dlg.isUIfor(alg, dataset)) {
                dialog = dlg;
                return dlg.getPanel();
            }
        }
        //last resort
        return new JPanel();
    }

    public ClusteringAlgorithm getAlgorithm() {
        String algName = (String) cbType.getSelectedItem();
        ClusteringAlgorithm algorithm = ClusteringFactory.getInstance().getProvider(algName);
        return algorithm;
    }

    public Props getProps() {
        if (dialog != null) {
            return dialog.getParams();
        } else {
            throw new RuntimeException("missing dialog");
        }
    }

    public Dataset<E> getSelectedDataset() {
        if (currDataset == null) {
            return dataset;
        }
        return currDataset;
    }

    public void setDataset(Dataset<E> dataset) {
        this.dataset = dataset;
        updateCheckbox(dataset);
    }

    private void updateCheckbox(Dataset<E> dataset) {
        cbData.removeAll();

        if (dataset != null) {
            cbData.addItem(dataset.getName());
            Iterator<String> iter = dataset.getChildIterator();
            String key;
            Dataset<? extends Instance> curr;
            while (iter.hasNext()) {
                key = iter.next();
                curr = dataset.getChild(key);
                if (curr != null) {
                    String name = curr.getName();
                    if (name != null) {
                        cbData.addItem(name);
                    }
                }
            }
        }

    }

}
