package org.clueminer.moleculepanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.Serializable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.clueminer.events.DatasetEvent;
import org.clueminer.events.DatasetListener;
import org.clueminer.hts.api.HtsInstance;
import org.clueminer.hts.api.HtsPlate;
import org.clueminer.molecule.MoleculeVizualizer;
import org.openscience.cdk.interfaces.IChemObject;

/**
 *
 * @author Tomas Barton
 */
public class MoleculesGroup extends JPanel implements DatasetListener, Serializable {

    private static final long serialVersionUID = -7149849370175201281L;


    public MoleculesGroup() {
        initComponents();
        //setBackground(Color.red);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
    }

    @Override
    public void datasetChanged(DatasetEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void datasetOpened(DatasetEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void datasetClosed(DatasetEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void datasetCropped(DatasetEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    //public void clusterSelected(DendrogramTree source, TreeCluster cluster, DendrogramMapping data) {
    public void clusterSelected(HtsPlate plate) {
        if (plate != null) {

                removeAll();
                int j = 0;
                HtsInstance inst;
                IChemObject molecule;
                JPanel panel;
            //  for (int i = cluster.firstElem; i <= cluster.lastElem; i++) {
            //  System.out.println("getting index: " + data.getRowIndex(i));
            //  inst = plate.instance(data.getRowIndex(i));

                    GridBagConstraints c = new GridBagConstraints();

                    c.anchor = GridBagConstraints.NORTH;
                    c.weightx = 0;
                    c.fill = GridBagConstraints.BOTH;
                    c.weighty = 1.0;
                    c.insets = new java.awt.Insets(0, 0, 0, 0);
                    c.gridx = 0;

            /*  molecule = inst.getMolecule();                    addLabel(inst.getName(), j++);
                    if (molecule != null) {
                        panel = new MoleculeVizualizer(inst.getMolecule());
                        c.gridy = j++;
                        add(panel, c);
                    }else{
                        addLabel("Missing molecule data", j++);

             }*/
            //     }


            revalidate();
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
