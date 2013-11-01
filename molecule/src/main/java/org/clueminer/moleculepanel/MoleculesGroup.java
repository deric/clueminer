package org.clueminer.moleculepanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.Serializable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Vector2d;
import org.clueminer.hts.api.HtsInstance;
import org.clueminer.hts.api.HtsPlate;
import org.clueminer.molecule.MoleculeVizualizer;
import org.openide.util.Exceptions;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.DeduceBondSystemTool;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.jchempaint.JChemPaintViewerPanel;

/**
 *
 * @author Tomas Barton
 */
public class MoleculesGroup extends JPanel implements Serializable {

    private static final long serialVersionUID = -7149849370175201281L;

    public MoleculesGroup() {
        initComponents();
        //setBackground(Color.red);
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

                    try {
                        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
                        IMolecule molecule = sp.parseSmiles(inst.getSmiles());

                        if (molecule != null) {
                            GridBagConstraints c = new GridBagConstraints();

                            c.anchor = GridBagConstraints.NORTH;
                            c.weightx = 0;
                            c.fill = GridBagConstraints.BOTH;
                            c.weighty = 1.0;
                            c.insets = new java.awt.Insets(0, 0, 0, 0);
                            c.gridx = 0;

                            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                            sdg.setMolecule(molecule);
                            sdg.generateCoordinates(new Vector2d(0, 1));
                            molecule = sdg.getMolecule();
                            molecule = new DeduceBondSystemTool().fixAromaticBondOrders(molecule);
                            //for some reason, smilesparser sets valencies, which we don't want in jcp
                            for (int i = 0; i < molecule.getAtomCount(); i++) {
                                molecule.getAtom(i).setValency(null);
                            }

                            IChemModel chemModel = DefaultChemObjectBuilder.getInstance()
                                    .newInstance(IChemModel.class);
                            chemModel.setMoleculeSet(DefaultChemObjectBuilder.getInstance()
                                    .newInstance(IMoleculeSet.class));
                            chemModel.getMoleculeSet().addAtomContainer(molecule);

                            panel = new JChemPaintViewerPanel(chemModel, 500, 500, true, true, null);


                            c.gridy = j++;
                            add(panel, c);
                        } else {
                            addLabel("Missing molecule data", j++);

                        }

                    } catch (InvalidSmilesException ise) {
                        Exceptions.printStackTrace(ise);
                    } catch (CDKException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
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

    public IMolecule parseSmiles(String smiles) {
        IMolecule molecule = null;
        try {
            SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
            molecule = sp.parseSmiles(smiles);


        } catch (InvalidSmilesException ise) {
            Exceptions.printStackTrace(ise);
        }
        return molecule;
    }
}
