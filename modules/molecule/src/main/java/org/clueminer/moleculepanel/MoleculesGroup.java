package org.clueminer.moleculepanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.Serializable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.clueminer.hts.api.HtsInstance;
import org.clueminer.hts.api.HtsPlate;
import org.clueminer.molecule.SmilesVisualizer;

/**
 *
 * @author Tomas Barton
 */
public class MoleculesGroup extends JPanel implements Serializable {

    private static final long serialVersionUID = -7149849370175201281L;

    public MoleculesGroup() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
    }

    public void plateUpdate(HtsPlate<HtsInstance> d) {
        if (d != null) {
            int j = 0;
            JPanel panel;
            System.out.println("hts plate: " + d.getName() + ", " + d.getId());
            for (HtsInstance inst : d) {
                System.out.println("instance: " + inst.getName());
                if (inst.hasFormula()) {
                    System.out.println("formula: " + inst.getSmiles());
                    String smiles = inst.getSmiles();

                    //panel = parser1(smiles);
                    panel = new SmilesVisualizer(smiles);

                    GridBagConstraints c = new GridBagConstraints();

                    c.anchor = GridBagConstraints.NORTH;
                    c.weightx = 0;
                    c.fill = GridBagConstraints.BOTH;
                    c.weighty = 1.0;
                    c.insets = new java.awt.Insets(0, 0, 0, 0);
                    c.gridx = 0;

                    c.gridy = j++;
                    add(panel, c);
                } else {
                    addLabel("Missing molecule data", j++);
                }
            }
        }
    }

    private void addLabel(String label, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 0;
        c.weighty = 0.0; //no fill while resize
        c.insets = new java.awt.Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = row;
        c.fill = GridBagConstraints.NONE;
        add(new JLabel(label), c);
    }
}
